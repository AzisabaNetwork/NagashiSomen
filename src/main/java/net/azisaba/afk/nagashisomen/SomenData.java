package net.azisaba.afk.nagashisomen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@Getter
@RequiredArgsConstructor
public class SomenData {

    private final Location location;
    private final Vector vector;
}
