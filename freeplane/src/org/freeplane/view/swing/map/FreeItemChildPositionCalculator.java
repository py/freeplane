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

import org.freeplane.view.swing.map.NodeViewLayoutAdapter.LayoutData;

/**
 * @author Dimitry Polivaev
 * 12.10.2013
 */
public class FreeItemChildPositionCalculator extends ChildPositionCalculator{
	public FreeItemChildPositionCalculator(int spaceAround, int vGap, NodeView child, int oldLevel, int level) {
        super(spaceAround, vGap, child, oldLevel, level);
    }
	@Override
    public void calcChildY(int childIndex, int yBefore, boolean visibleChildAlreadyFound, final boolean calculateOnLeftSide, final LayoutData data, final int[] levels, final GroupMargins[] groups) {
		initY(yBefore, visibleChildAlreadyFound, data, childIndex);
		calcFreeItemY(data, childIndex);
	}
	protected void calcFreeItemY(final LayoutData data, int i) {
	    data.ly[i] = childShiftY - childContentShift - childCloudHeigth / 2 - getSpaceAround();
    }

}