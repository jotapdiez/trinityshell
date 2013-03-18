/*
 * Trinity Window Manager and Desktop Shell Copyright (C) 2012 Erik De Rijcke
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.trinity.shell.api.surface;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import org.trinity.foundation.api.shared.Coordinate;
import org.trinity.shell.api.scene.AbstractShellNode;
import org.trinity.shell.api.scene.ShellNode;
import org.trinity.shell.api.scene.event.ShellNodeChildAddedEvent;
import org.trinity.shell.api.scene.event.ShellNodeChildLeftEvent;
import org.trinity.shell.api.scene.event.ShellNodeEvent;
import org.trinity.shell.api.scene.manager.ShellLayoutManager;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListeningExecutorService;

import static com.google.common.base.Preconditions.checkArgument;

/***************************************
 * An {@link AbstractShellSurface} that can have child {@link ShellNode}s.
 * 
 *************************************** 
 */
// TODO make threadSafe
@NotThreadSafe
public abstract class AbstractShellSurfaceParent extends AbstractShellSurface implements ShellSurfaceParent {

	private final Set<AbstractShellNode> children = new HashSet<AbstractShellNode>();

	private Optional<ShellLayoutManager> optionalLayoutManager = Optional.absent();

	protected AbstractShellSurfaceParent(final ListeningExecutorService shellExecutor) {
		super(shellExecutor);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The returned array is a copy of the internal array.
	 */
	@Override
	public AbstractShellNode[] getChildrenImpl() {
		return this.children.toArray(new AbstractShellNode[this.children.size()]);
	}

	@Override
	protected void updateChildrenPosition() {
		for (final AbstractShellNode child : getChildrenImpl()) {
			final Coordinate childPosition = child.getPositionImpl();
			child.getShellNodeExecutor().move(childPosition);
		}
	}

	@Override
	public Optional<ShellLayoutManager> getLayoutManagerImpl() {
		return this.optionalLayoutManager;
	}

	@Override
	public Void setLayoutManagerImpl(final ShellLayoutManager shellLayoutManager) {
		this.optionalLayoutManager = Optional.of(shellLayoutManager);
		addListenerImpl(shellLayoutManager);
		return null;
	}

	@Override
	protected void doMove(final boolean execute) {
		super.doMove(execute);
		updateChildrenPosition();
	}

	@Override
	protected void doMoveResize(final boolean execute) {
		super.doMoveResize(execute);
		updateChildrenPosition();
		layout();
	}

	@Override
	protected void doResize(final boolean execute) {
		super.doResize(execute);
		layout();
	}

	@Override
	public Void handleChildReparentEventImpl(final ShellNode child) {
		checkArgument(child instanceof AbstractShellNode);

		ShellNodeEvent shellNodeEvent;
		if (this.children.contains(child)) {
			this.children.remove(child);
			// child.removeShellNodeEventHandler(this);
			shellNodeEvent = new ShellNodeChildLeftEvent(	this,
															toGeoTransformationImpl());
			postImpl(shellNodeEvent);
		} else {
			this.children.add((AbstractShellNode) child);
			shellNodeEvent = new ShellNodeChildAddedEvent(	this,
															toGeoTransformationImpl());
			postImpl(shellNodeEvent);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This call has no effect if no {@link ShellLayoutManager} is set for this
	 * node.
	 */
	@Override
	public Void layoutImpl() {
		final Optional<ShellLayoutManager> optionalLayoutManager = getLayoutManagerImpl();
		if (optionalLayoutManager.isPresent()) {
			optionalLayoutManager.get().layout(this);
		}
		return null;
	}
}