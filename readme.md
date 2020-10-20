# TRCinematics

Easy custom cinematic creation for developers.

## Example

```java
            World w = event.getPlayer().getWorld();
            CinematicPath path1 = CinematicPath.builder().ticks(80)
                    .point(new Location(w, 0, 100, 0, -90f, 0))
                    .point(new Location(w, 30, 80, 0, -90f, 25))
                    .point(new Location(w, 60, 120, 0, -90f, -25))
                    .build();
            TRCinematic cinematic = TRCinematic.builder()
                    .path(path1)
                    .title(20, new Title("Some Title", "subtitle"))
                    .customFunction(40, inst -> inst.getPlayers().forEach(p -> p.sendMessage("Half way there")))
                    .onFinished(inst -> inst.getPlayers().forEach(p -> p.sendMessage("Done")))
                    .build();
            TRCinematics.instance.playCinematic(cinematic, Arrays.asList(event.getPlayer()));
```