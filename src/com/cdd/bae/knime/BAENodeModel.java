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

import java.io.*;

import org.knime.core.data.*;
import org.knime.core.data.def.*;
import org.knime.core.node.*;
import org.knime.core.node.defaultnodesettings.*;

/**
 * This is the model implementation of BioAssayExpress.
 * Download content from BioAssay Express servers.
 *
 * @author Collaborative Drug Discovery, Inc.
 */
 
public class BAENodeModel extends NodeModel
{
	private static final NodeLogger logger = NodeLogger.getLogger(BAENodeModel.class);
	
	public static String CFGKEY_SITEURL = "SiteURL";
	public static String CFGKEY_QUERY = "Query";

	public static final String DEFAULT_SITEURL = "https://beta.bioassayexpress.com";
	public static final String DEFAULT_QUERY = "()"; // (bao:BAO_0002854=@bao:BAO_0000009)

	private final SettingsModelString cfgSiteURL = new SettingsModelString(CFGKEY_SITEURL, DEFAULT_SITEURL);
	private final SettingsModelString cfgQuery = new SettingsModelString(CFGKEY_QUERY, DEFAULT_QUERY);

	protected BAENodeModel()
	{
		super(0, 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec) throws Exception
	{
		AssayDownload download = new AssayDownload(cfgSiteURL.getStringValue(), cfgQuery.getStringValue());
		download.obtain();
	
		int ncells = 3;
		DataColumnSpec[] columns = new DataColumnSpec[ncells];
		columns[0] = new DataColumnSpecCreator("AssayID", LongCell.TYPE).createSpec();
		columns[1] = new DataColumnSpecCreator("UniqueID", StringCell.TYPE).createSpec();
		columns[2] = new DataColumnSpecCreator("SchemaURI", StringCell.TYPE).createSpec();
				
		DataTableSpec outputSpec = new DataTableSpec(columns);
		BufferedDataContainer container = exec.createDataContainer(outputSpec);
		
		AssayDownload.Assay[] assays = download.getAssays();
		for (int n = 0; n < assays.length; n++)
		{
			RowKey key = new RowKey("Row#" + (n + 1));
			
			DataCell[] cells = new DataCell[ncells];
			cells[0] = new LongCell(assays[n].assayID);
			cells[1] = new StringCell(assays[n].uniqueID);
			cells[2] = new StringCell(assays[n].schemaURI);

			DataRow row = new DefaultRow(key, cells);
			container.addRowToTable(row);

			exec.checkCanceled();
			exec.setProgress(n / (double)assays.length);
		}
		
		container.close();
		
		return new BufferedDataTable[]{container.getTable()};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset()
	{
		// TODO Code executed on reset.
		// Models build during execute are cleared here.
		// Also data handled in load/saveInternals will be erased here.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException
	{
		return new DataTableSpec[]{null};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings)
	{
		cfgSiteURL.saveSettingsTo(settings);
		cfgQuery.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException
	{
		cfgSiteURL.loadSettingsFrom(settings);
		cfgQuery.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException
	{
		cfgSiteURL.validateSettings(settings);
		cfgQuery.validateSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException
	{
	}
}
