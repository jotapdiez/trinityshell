/*
 * This file is part of HyperDrive.
 * 
 * HyperDrive is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * HyperDrive is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * HyperDrive. If not, see <http://www.gnu.org/licenses/>.
 */
package org.trinity.shell.protocol;

import org.trinity.shell.foundation.api.RenderArea;

//TODO documentation
/**
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public interface DesktopProtocol {

	void registerClient(RenderArea client);

	/**
	 * 
	 * @param client
	 * @return true if the underlying protocol implements this call.
	 */
	boolean offerInput(RenderArea client);

	/**
	 * 
	 * @param client
	 * @return true if the underlying protocol implements this call.
	 */
	boolean requestDelete(RenderArea client);

	ProtocolEvent query(RenderArea client, ProtocolEventType eventType);

	void updateProtocolEvent(RenderArea client, ProtocolEvent protocolEvent);
}