/*
 * ============================================================================
 * =- Criminalysis -=- A crime analysis toolbox -=- (c) 2024+ Laurent Menten -=
 * ============================================================================
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <https://www.gnu.org/licenses/>.
 * ============================================================================
 */

module Criminalysis
{
	requires org.jetbrains.annotations;

	requires java.datatransfer;
	requires java.desktop;
	requires java.instrument;
	requires java.management;
	requires java.rmi;
	requires java.logging;
	requires java.sql;

	requires jdk.attach;
	requires jdk.jdi;

	requires lib.lmenten;
	requires lib.lmenten.swing;

	requires swingx.all;
	requires org.jxmapviewer.jxmapviewer2;

	requires com.formdev.flatlaf;
	requires com.formdev.flatlaf.extras;

	requires org.openjdk.nashorn;

	requires auto.common;
	requires com.google.auto.service;

	requires org.reflections;
	requires java.prefs;
	requires com.miglayout.swing;
	requires org.kordamp.ikonli.swing;
	requires org.kordamp.ikonli.win10;

	requires jaretutil;

	requires timebars;

	requires com.sun.jna;
	requires com.sun.jna.platform;
	requires uk.co.caprica.vlcj;

	exports javassist.bytecode to org.reflections;
	exports javassist.bytecode.annotation to org.reflections;

	exports be.lmenten.criminalysis.plugins;
	uses be.lmenten.criminalysis.plugins.Plugin;
}
