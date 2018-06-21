/*
	BioAssay Express (BAE) KNIME Plugin

	(c) 2016-2018 Collaborative Drug Discovery Inc.
	All rights reserved
	
	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License 2.0
	as published by the Free Software Foundation:
	 
	http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
	 
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Lesser General Public License for more details.
	
	You should have received a copy of the GNU Lesser General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.	
*/

package com.cdd.bae.knime;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "BioAssayExpress" Node.
 * Download content from BioAssay Express servers.
 *
 * @author Collaborative Drug Discovery, Inc.
 */
public class BAENodeView extends NodeView<BAENodeModel>
{

	/**
	 * Creates a new view.
	 * 
	 * @param nodeModel The model (class: {@link BAENodeModel})
	 */
	protected BAENodeView(final BAENodeModel nodeModel)
	{
		super(nodeModel);

		// TODO instantiate the components of the view here.

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void modelChanged()
	{

		// TODO retrieve the new model from your nodemodel and 
		// update the view.
		BAENodeModel nodeModel = (BAENodeModel)getNodeModel();
		assert nodeModel != null;

		// be aware of a possibly not executed nodeModel! The data you retrieve
		// from your nodemodel could be null, emtpy, or invalid in any kind.

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onClose()
	{

		// TODO things to do when closing the view
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onOpen()
	{

		// TODO things to do when opening the view
	}

}
