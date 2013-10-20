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
public class SummaryChildPositionCalculator extends ChildPositionCalculator{
	public SummaryChildPositionCalculator(int spaceAround, int vGap, NodeView child, int oldLevel, int level) {
        super(spaceAround, vGap, child, oldLevel, level);
    }
	@Override
    public void calcChildY(int childIndex, int yBefore, boolean visibleChildAlreadyFound, final boolean calculateOnLeftSide, final LayoutData data, final int[] levels, final GroupMargins[] groups) {
        final GroupMargins groupMargins = groups[level - 1];
		if (child.isFirstGroupNode()) {
			groups[level].beginFrom(groupMargins.start);
		}
		int summaryY = (groupMargins.startY + groupMargins.endY) / 2 - childContentHeight / 2
		        + childShiftY - (child.getContent().getY() - childCloudHeigth / 2 - getSpaceAround());
		childBeginY = summaryY;
		data.ly[childIndex] = childBeginY;
		childEndY = yBefore;
		if (!child.isFree()) {
			final int groupShiftRequiredBySummary = groupMargins.startY - summaryY - child.getTopOverlap();
			if (groupShiftRequiredBySummary > 0) {
				childEndY += groupShiftRequiredBySummary;
				summaryY += groupShiftRequiredBySummary;
				for (int j = groupMargins.start; j <= childIndex; j++) {
					NodeView groupItem = (NodeView) child.getParent().getComponent(j);
					if (groupItem.isLeft() == calculateOnLeftSide && (data.summary[j] || !data.free[j]))
						data.ly[j] += +groupShiftRequiredBySummary;
				}
			}
			if (childHeight != 0) {
				summaryY += childHeight + getVGap() - child.getBottomOverlap();
			}
			childEndY = Math.max(childEndY, summaryY);
			if (groupShiftRequiredBySummary > 0)
				topChange -= groupShiftRequiredBySummary;
		}
	}

	protected void calculateSummaryChildContentHeightSum(final int[] groupStartContentHeightSum) {
	    final int itemLevel = level - 1;
	    if (child.isFirstGroupNode())
	        groupStartContentHeightSum[level] = groupStartContentHeightSum[itemLevel];
	    if (!child.isFree()) {
	    	final int summaryContentHeight = groupStartContentHeightSum[itemLevel] + childContentHeight;
	    	if (childContentHeightSum < summaryContentHeight) {
	    		childContentHeightSum = summaryContentHeight;
	    	}
	    }
    }

	@Override
    public void chilContentHeightSum(final int[] groupStartContentHeightSum, boolean pVisibleChildFound, int childContentHeightSumBefore) {
		childContentHeightSum = childContentHeightSumBefore;
	    calculateSummaryChildContentHeightSum(groupStartContentHeightSum);
    }

}