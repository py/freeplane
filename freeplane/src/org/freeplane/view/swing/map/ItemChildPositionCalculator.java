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
 * 12.10.2013
 */
public class ItemChildPositionCalculator extends ChildPositionCalculator{
	public ItemChildPositionCalculator(int spaceAround, int vGap, NodeView child, int previousChildLevel, int level) {
        super(spaceAround, vGap, child, previousChildLevel, level);
    }

	@Override
    public void calcChildY(int childIndex, int yBefore, boolean visibleChildAlreadyFound, final boolean calculateOnLeftSide, final int[] levels, final GroupMargins[] groups) {
		topChange = 0;
		this.childBeginY = yBefore;
		if (childShiftY < 0 || !visibleChildAlreadyFound)
		    topChange += childShiftY;
		childBeginY -= child.getTopOverlap();
		if (childShiftY < 0) {
			childEndY = childBeginY;
			childEndY -= childShiftY;
		}
		else {
			if (visibleChildAlreadyFound)
				childBeginY += childShiftY;
			childEndY = childBeginY;
		}
		if (childHeight != 0) {
			childEndY += childHeight + getVGap();
			childEndY -= child.getBottomOverlap();
		}
		topChange -= childContentShift;
		topChange += child.getTopOverlap();
	}

	@Override
    public void calcChildContentHeightSum(final int[] groupStartContentHeightSum, boolean pVisibleChildFound, int childContentHeightSumBefore) {
		childContentHeightSum = childContentHeightSumBefore + childContentHeight;
		final boolean followsSummary = this.previousChildLevel > 0;
		if (followsSummary)
		    for (int j = 0; j < this.previousChildLevel; j++)
				groupStartContentHeightSum[j] = this.childContentHeightSum;
		else if (this.child.isFirstGroupNode()) {
			groupStartContentHeightSum[0] = this.childContentHeightSum;
		}
		if (this.childHeight != 0 && pVisibleChildFound)
		    this.childContentHeightSum += this.getVGap();
    }
}