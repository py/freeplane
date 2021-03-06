/**
 * 
 */
package org.freeplane.plugin.script.proxy;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.freeplane.features.link.LinkModel;
import org.freeplane.features.link.NodeLinks;
import org.freeplane.features.map.NodeModel;
import org.freeplane.plugin.script.ScriptContext;

class ConnectorOutListProxy extends AbstractCollection<Proxy.Connector> {
	private final NodeModel node;
	private final ScriptContext scriptContext;

	public ConnectorOutListProxy(final NodeProxy nodeProxy) {
		this.node = nodeProxy.getDelegate();
		this.scriptContext = nodeProxy.getScriptContext();
	}

	@Override
	public Iterator<Proxy.Connector> iterator() {
		return new ConnectorIterator(Collections.unmodifiableList(new ArrayList<LinkModel>(NodeLinks.getLinks(node)))
		    .iterator(), scriptContext);
	}

	@Override
	public int size() {
		return NodeLinks.getLinks(node).size();
	}
}
