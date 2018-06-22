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

import org.knime.core.node.defaultnodesettings.*;

/**
 * <code>NodeDialog</code> for the "BioAssayExpress" Node.
 * Download content from BioAssay Express servers.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Collaborative Drug Discovery, Inc.
 */
public class BAENodeDialog extends DefaultNodeSettingsPane
{

	/**
	 * New pane for configuring BioAssayExpress node dialog.
	 * This is just a suggestion to demonstrate possible default dialog
	 * components.
	 */
	protected BAENodeDialog()
	{
		super();

		SettingsModelString modelSiteURL = new SettingsModelString(BAENodeModel.CFGKEY_SITEURL, BAENodeModel.DEFAULT_SITEURL);
		SettingsModelString modelQuery = new SettingsModelString(BAENodeModel.CFGKEY_QUERY, BAENodeModel.DEFAULT_QUERY);
		SettingsModelBoolean modelWithLabels = new SettingsModelBoolean(BAENodeModel.CFGKEY_WITHLABELS, BAENodeModel.DEFAULT_WITHLABELS);

		DialogComponentString dlgSiteURL = new DialogComponentString(modelSiteURL, "Site URL:", true, 80);
		DialogComponentString dlgQuery = new DialogComponentString(modelQuery, "Query:", false, 80);
		DialogComponentBoolean dlgWithLabels = new DialogComponentBoolean(modelWithLabels, "With Labels");
		dlgSiteURL.setToolTipText("Base URL for BioAssayExpress server, e.g. http://www.bioassayexpress.com");
		dlgQuery.setToolTipText("Assay query string. If blank, acquires all assays. See the Explore and Search pages for composing queries.");
		dlgWithLabels.setToolTipText("Add extra columns to show the labels to go with the URIs.");

		addDialogComponent(dlgSiteURL);
		addDialogComponent(dlgQuery);
		addDialogComponent(dlgWithLabels);
	}
}
