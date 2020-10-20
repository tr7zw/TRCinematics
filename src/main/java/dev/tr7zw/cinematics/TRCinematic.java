package dev.tr7zw.cinematics;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.github.paperspigot.Title;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Builder
@Getter
public class TRCinematic {

	@Singular
	private List<CinematicPath> paths;
	@Singular
	private Map<Integer, Title> titles;
	@Singular
	private Map<Integer, Consumer<CinematicInstance>> customFunctions;
	@Builder.Default
	private boolean resetPlayers = true;
	@Builder.Default
	private Consumer<CinematicInstance> onFinished = (cin) -> {};
	
}
