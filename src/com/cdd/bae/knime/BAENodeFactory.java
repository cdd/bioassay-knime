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

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "BioAssayExpress" Node.
 * Download content from BioAssay Express servers.
 *
 * @author Collaborative Drug Discovery, Inc.
 */
public class BAENodeFactory extends NodeFactory<BAENodeModel>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BAENodeModel createNodeModel()
	{
		return new BAENodeModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNrNodeViews()
	{
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeView<BAENodeModel> createNodeView(final int viewIndex, final BAENodeModel nodeModel)
	{
		return new BAENodeView(nodeModel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasDialog()
	{
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeDialogPane createNodeDialogPane()
	{
		return new BAENodeDialog();
	}

}
