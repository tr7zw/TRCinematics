package dev.tr7zw.cinematics;

import java.util.List;

import org.bukkit.Location;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Builder
@Getter
public class CinematicPath {

	@Singular
	private List<Location> points;
	private int ticks;
	
}
