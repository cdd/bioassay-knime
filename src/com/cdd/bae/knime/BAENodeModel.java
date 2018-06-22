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
import java.util.*;

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
	public static String CFGKEY_WITHLABELS = "WithLabels";

	public static final String DEFAULT_SITEURL = "https://beta.bioassayexpress.com";
	public static final String DEFAULT_QUERY = ""; // (bao:BAO_0002854=@bao:BAO_0000009)
	public static final boolean DEFAULT_WITHLABELS = true;

	private final SettingsModelString cfgSiteURL = new SettingsModelString(CFGKEY_SITEURL, DEFAULT_SITEURL);
	private final SettingsModelString cfgQuery = new SettingsModelString(CFGKEY_QUERY, DEFAULT_QUERY);
	private final SettingsModelBoolean cfgWithLabels = new SettingsModelBoolean(CFGKEY_WITHLABELS, DEFAULT_WITHLABELS);

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
		try {return perform(inData, exec);}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
	}
	
	private BufferedDataTable[] perform(final BufferedDataTable[] inData, final ExecutionContext exec) throws Exception
	{
		AssayDownload download = new AssayDownload(cfgSiteURL.getStringValue(), cfgQuery.getStringValue());
		download.obtain();
		AssayDownload.Assay[] assays = download.getAssays();

		boolean withLabels = cfgWithLabels.getBooleanValue();
System.out.println("LABELS:"+withLabels);

		// figure out what's going into the columns

		int headerCols = 3, ncolumns = headerCols;
		Map<String, Integer> assnColumn = new HashMap<>(); // hash-to-column#
		Map<String, Integer> assnLabel = new HashMap<>(); // as above, but for the label version
		List<String> columnNames = new ArrayList<>(); // quasi-readable names for applicable assignments
		for (AssayDownload.Assay assay : download.getAssays()) for (AssayDownload.Annotation annot : assay.annotations)
		{
			String hash = annot.assnHash();
			if (assnColumn.containsKey(hash)) continue;

			assnColumn.put(hash, ncolumns++);			
			String colName = "<" + annot.propURI + ">";
			if (annot.groupNest != null) for (String uri : annot.groupNest) colName += ",<" + uri + ">";
			columnNames.add(colName);
			
			if (withLabels) 
			{
				assnLabel.put(hash, ncolumns++);
				columnNames.add(annot.propLabel);
			}
			
		}
		int annotCols = ncolumns - headerCols;
		
		DataColumnSpec[] columns = new DataColumnSpec[ncolumns];
		columns[0] = new DataColumnSpecCreator("AssayID", LongCell.TYPE).createSpec();
		columns[1] = new DataColumnSpecCreator("UniqueID", StringCell.TYPE).createSpec();
		columns[2] = new DataColumnSpecCreator("SchemaURI", StringCell.TYPE).createSpec();
		for (int n = 0, idx = headerCols; n < columnNames.size(); n++)
			columns[idx++] = new DataColumnSpecCreator(columnNames.get(n), StringCell.TYPE).createSpec();
				
		// populate the table itself
				
		DataTableSpec outputSpec = new DataTableSpec(columns);
		BufferedDataContainer container = exec.createDataContainer(outputSpec);
		
		for (int n = 0; n < assays.length; n++)
		{
			RowKey key = new RowKey("Row#" + (n + 1));
			
			DataCell[] cells = new DataCell[ncolumns];
			cells[0] = new LongCell(assays[n].assayID);
			cells[1] = new StringCell(assays[n].uniqueID);
			cells[2] = new StringCell(assays[n].schemaURI);
			
			// fill in all of the cells that represent annotation values
			String[] content = new String[annotCols];
			for (int i = 0; i < annotCols; i++) content[i] = "";
			for (AssayDownload.Annotation annot : assays[n].annotations)
			{
				int i = assnColumn.get(annot.assnHash()) - 3;
				String sep = content[i].length() != 0 ? "\n" : "";
				if (annot.valueURI != null && annot.valueURI.length() > 0)
					content[i] += sep + "<" + annot.valueURI + ">";
				else if (annot.valueLabel != null && annot.valueLabel.length() > 0 && !withLabels)
					content[i] += sep + '"' + annot.valueLabel + '"'; 
					
				if (withLabels)
				{
					i = assnLabel.get(annot.assnHash()) - 3;
					content[i] += sep + (annot.valueLabel == null ? "" : annot.valueLabel);
				}
			}
			
			for (int i = headerCols; i < ncolumns; i++) cells[i] = new StringCell(content[i - headerCols]);

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
		cfgWithLabels.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException
	{
		cfgSiteURL.loadSettingsFrom(settings);
		cfgQuery.loadSettingsFrom(settings);
		cfgWithLabels.loadSettingsFrom(settings);
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
