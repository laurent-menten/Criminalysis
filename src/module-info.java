module Criminalysis
{
	exports be.lmenten.criminalysis.plugins;
	requires java.datatransfer;
	requires java.desktop;
	requires java.instrument;
	requires java.management;
	requires java.rmi;
	requires jdk.attach;
	requires jdk.jdi;
	requires swingx.all;
	requires com.formdev.flatlaf;
	requires lib.lmenten;
	requires java.logging;
	requires org.jetbrains.annotations;
	requires java.sql;
	requires com.formdev.flatlaf.extras;
	requires lib.lmenten.swing;
	requires org.openjdk.nashorn;
	requires org.reflections;
	requires org.jxmapviewer.jxmapviewer2;

	uses be.lmenten.criminalysis.plugins.Plugin;
}