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

import org.freeplane.features.cloud.CloudModel;
import org.freeplane.view.swing.map.NodeViewLayoutAdapter.LayoutData;
import org.freeplane.view.swing.map.cloud.CloudView;

/**
 * @author Dimitry Polivaev
 * 12.10.2013
 */
abstract class ChildPositionCalculator{
	public static ChildPositionCalculator create(int spaceAround, int vGap, NodeView child, int oldLevel, int level) {
	    if (level == 0) {
	    	if (child.isFree())
	            return new FreeItemChildPositionCalculator(spaceAround, vGap, child, oldLevel, level);
            else
	            return new ItemChildPositionCalculator(spaceAround, vGap, child, oldLevel, level);
	    }
        else
        	if (child.isFree())
        		return new FreeSummaryChildPositionCalculator(spaceAround, vGap, child, oldLevel, level);
        	else
        		return new SummaryChildPositionCalculator(spaceAround, vGap, child, oldLevel, level);
    }

	protected final NodeView child;
	protected final int oldLevel;
	protected final int level;
	protected final int vGap;
	protected final int spaceAround;
	protected final int childHeight;
	protected final int childCloudHeigth;
	protected final int childContentHeight;
	protected final int childShiftY;
	protected final int childContentShift;
	protected final boolean isItem;

	protected int top;
	public int getTopChange() {
		return top;
	}

	public int getY() {
		return y;
	}

	public boolean isVisibleChildFound() {
		return visibleChildFound;
	}

	protected int y;
	protected boolean visibleChildFound;


	public ChildPositionCalculator(int spaceAround, int vGap, NodeView child, int oldLevel, int level) {
		this.vGap = vGap;
		this.spaceAround = spaceAround;
		this.child = child;
		this.oldLevel = oldLevel;
		this.level = level;
		childHeight = child.getHeight() - 2 * spaceAround;
		childCloudHeigth = getAdditionalCloudHeigth();
		childContentHeight = child.getContent().getHeight() + childCloudHeigth;
		childShiftY = child.isContentVisible() ? child.getShift() : 0;
		childContentShift = child.getContent().getY() - childCloudHeigth / 2 -spaceAround;
		isItem = level == 0;
    }

	public int getAdditionalCloudHeigth() {
		if (!child.isContentVisible()) {
			return 0;
		}
		final CloudModel cloud = child.getCloudModel();
		if (cloud != null) {
			return CloudView.getAdditionalHeigth(cloud, child);
		}
		else {
			return 0;
		}
	}

	public void calcChildY(int yBefore, boolean visibleChildAlreadyFound, final boolean calculateOnLeftSide, final LayoutData data, final int[] levels, final GroupMargins[] groups, int i) {
		initY(yBefore, visibleChildAlreadyFound, data, i);
    }

	protected void initY(int yBefore, boolean visibleChildAlreadyFound, final LayoutData data, int i) {
	    this.top = 0;
		this.y = yBefore;
		this.visibleChildFound = visibleChildAlreadyFound;
	    data.summary[i] = !isItem;
    }

	protected void setGroupMargins(final LayoutData data, final GroupMargins[] groups, int i) {
		groups[level].setMargins(child.isFirstGroupNode(), data.ly[i] + child.getTopOverlap(), data.ly[i] + childHeight - child.getBottomOverlap());
    }

	protected int getVGap() {
	    return vGap;
    }

	protected int getSpaceAround() {
	    return spaceAround;
    }
}