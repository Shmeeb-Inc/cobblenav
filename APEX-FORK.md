# Apex Cobblemon fork of Cobblenav

This is a fork of [gatekeep06/cobblenav](https://github.com/gatekeep06/cobblenav)
maintained for the Apex Cobblemon server. Cobblenav is licensed under MPL-2.0;
this repository is the published source for the modified jar distributed to
players and run on the server, per MPL-2.0 §3.2.

## What this fork changes

The goal is a server that runs Cobblenav for players who have it, while players
with only Cobblemon (no Cobblenav) can still join.

1. **No custom items** — item, creative-tab, and wanderer-trade registration is
   a no-op (`CobblenavFabric`), loot injection is disabled, and the recipe /
   loot-table / item-tag data files are removed. Modded entries in the item
   registry would kick clients without the mod during Fabric registry sync.
   Item *classes* are intentionally kept (dead code) to keep the diff small.
2. **Clientbound packets are gated** on the client having announced the mod's
   payload channels (`Implementation.canSendToPlayer`). This covers the
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
merges should be near-automatic. After merging, check:

- `CobblenavFabric.registerItems` / `injectLootTables` are still no-ops
- new clientbound send sites (grep `sendToPlayer`) either go through
  `CobblenavNetworkPacket` or are gated manually
- new data files under `data/cobblenav/recipe|loot_table|tags` referencing the
  unregistered items are removed
- bump the version suffix in `gradle.properties` (e.g. `2.4.0+apex.1`)

## Building

```
./gradlew :fabric:remapJar
```

The jar lands in `fabric/build/libs/cobblenav-fabric-<version>.jar` and goes in
the mods folder on both the server and the client modpack profile.
