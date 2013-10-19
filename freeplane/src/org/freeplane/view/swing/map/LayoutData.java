/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2013 Dimitry
 *
 *  This file author is Dimitry
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.view.swing.map;

/**
 * @author Dimitry Polivaev
 * 19.10.2013
 */
class LayoutData {
	final int[] lx;
	final int[] ly;
	final boolean[] free;
	final boolean[] summary;
	int left;
	int childContentHeight;
	int top;
	boolean firstDataSet;

	public LayoutData(int childCount) {
		super();
		this.lx = new int[childCount];
		this.ly = new int[childCount];
		this.free = new boolean[childCount];
		this.summary = new boolean[childCount];
		this.left = 0;
		this.childContentHeight = 0;
		this.top = 0;
		firstDataSet = false;
	}
}