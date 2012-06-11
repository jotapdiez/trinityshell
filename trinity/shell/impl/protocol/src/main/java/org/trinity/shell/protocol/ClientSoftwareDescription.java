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

//TODO documentation
/**
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public class ClientSoftwareDescription implements ProtocolEvent {

	public static final ProtocolEventType TYPE = new ProtocolEventType();

	private final String className;
	private final String clientInstanceName;

	public ClientSoftwareDescription(final String className,
			final String clientInstanceName) {
		this.className = className;
		this.clientInstanceName = clientInstanceName;
	}

	public String getClassName() {
		return this.className;
	}

	public String getClientInstanceName() {
		return this.clientInstanceName;
	}

	@Override
	public ProtocolEventType getType() {
		return ClientSoftwareDescription.TYPE;
	}
}