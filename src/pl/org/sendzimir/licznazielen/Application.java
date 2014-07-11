package pl.org.sendzimir.licznazielen;

import java.util.List;

import roboguice.application.RoboApplication;

import com.google.inject.Module;

public class Application extends RoboApplication {
	protected void addApplicationModules(List<Module> modules) {
		modules.add(new RealModule());
	}
}
