# Apex Cobblemon fork of Cobblenav

This is a fork of [gatekeep06/cobblenav](https://github.com/gatekeep06/cobblenav)
maintained for the Apex Cobblemon server. Cobblenav is licensed under MPL-2.0;
this repository is the published source for the modified jar distributed to
players and run on the server, per MPL-2.0 §3.2.

## What this fork changes

The goal is a server that runs Cobblenav for players who have it, while players
with only Cobblemon (no Cobblenav) can still join.

1. **No custom items** — item, creative-tab, and wanderer-trade registration is
   a no-op (`CobbleNavFabric`), loot injection is disabled, and the recipe /
   loot-table / item-tag data files are removed. Modded entries in the item
   registry would kick clients without the mod during Fabric registry sync.
   Item *classes* are intentionally kept (dead code) to keep the diff small.
2. **Clientbound packets are gated** on the client having announced the mod's
   payload channels (`CobbleNavImplementation.canSendToPlayer`). This covers the
   `CobblenavNetworkPacket.sendToPlayer` funnel, the label/EV-yield
   `DataRegistrySyncPacket`s, and — critically — the spawn-data player store,
   which rides Cobblemon's `SetClientPlayerDataPacket` whose decode throws (and
   disconnects the client) on unknown store types (`ClientGatedPlayerDataFactory`,
   `SpawnDataCatalogue.onCatalogueUpdated`).
3. **`/pokenav open`** — with the items gone, this command is the entry point to
   the Pokénav UI. It sends the same `OpenPokenavPacket` the item's use action
   sent. The Apex Integration client mod binds a key (default `N`) that runs it.
4. The PokéFinder and Finder screen still exist in the code but are unreachable
   (their only entry point was the unregistered PokéFinder item).
5. **UI tweaks** for the pokedex-style frame art: the radial menu is hidden
   (`PokenavScreen.addDefaultBottomWidgets` defaults `includeRadialMenu` to
   false), the status bar sits a few px higher, the Location screen layout is
   nudged to clear the bezel, every reachable button has a tooltip, tooltips are
   drawn through a deferred pass in `PokenavScreen` so they render above the
   frame, the inventory key closes the screen, and there is no opening slide
   animation. The `pokenav_details` overlay texture is removed.

Only the `common` and `fabric` modules are maintained; `neoforge` is untouched
and unused.

## Merging upstream updates

```
git fetch upstream
git checkout apex
git merge <new-upstream-tag>
```

The patch set is deliberately small (a handful of no-op'd registration sites,
one gate in the packet funnel, and additive files marked "Apex fork"), so
merges should be near-automatic. Upstream formats with spotless (tabs, trailing
commas, `I18nUtil.label(...)` for lang keys); write fork additions in that
style so the diff stays quiet. After merging, check:

- `CobbleNavFabric.registerItems` / `injectLootTables` are still no-ops and
  `onInitialize` registers no wanderer trades
- **nothing class-initializes `CobbleNavItems`** (grep for references). Its
  static init constructs `Item` objects, and since 1.20.5 an `Item` constructor
  creates an intrusive registry holder — that throws once the item registry is
  frozen, and the fork never registers the items. This is why
  `ModelBakeryMixin.injectInit` loads only the track-arrow model by location,
  `PokeRodItemMixin.injectUse` is empty, and `TrackArrowOverlay` renders that
  model with a dummy `Items.STICK` stack.
- new clientbound send sites (grep `sendToPlayer`) either go through
  `CobblenavNetworkPacket` or are gated manually
- new data files under `data/cobblenav/recipe|loot_table|tags` referencing the
  unregistered items are removed
- new screens take their bottom widgets from
  `PokenavScreen.addDefaultBottomWidgets` (or otherwise skip `RadialPopupMenu`)
- bump the version suffix in `gradle.properties` (e.g. `2.4.0+apex.1`)

## Building

```
./gradlew :fabric:remapJar
```

The fork's wrapper is on Gradle 9.4, so the shadow plugin is the maintained
`com.gradleup.shadow` (upstream still pins `com.github.johnrengelman.shadow`
8.1.1 on Gradle 8.14, which fails at `shadowJar` under Gradle 9). Expect a
trivial conflict in those three plugin lines on each merge; keep the gradleup
side.

The jar lands in `fabric/build/libs/cobblenav-fabric-<version>.jar` (or in
`CLIENT_BUILD_PATH` when that Gradle property is set, e.g. in
`~/.gradle/gradle.properties`) and goes in the mods folder on both the server
and the client modpack profile.
