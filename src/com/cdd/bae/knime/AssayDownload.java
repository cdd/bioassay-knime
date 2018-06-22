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
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;

import org.json.*;


/**
 * Connects to a BAE server and downloads all of the assays corresponding to the given query.
 */
public class AssayDownload
{
	private String siteURL; // the base URL for the BAE site (e.g. "https://www.bioassayexpress.com")
	private String query; // the assay selector query (e.g. "()" for all, "(bao:BAO_0002854=@bao:BAO_0000009)" for a simple match)

	public static class Annotation
	{
		public String valueURI, valueLabel;
		public String propURI, propLabel;
		public String[] groupNest, groupLabel;
		
		public String assnHash()
		{
			return propURI + "::" + (groupNest == null ? "" : String.join("::", groupNest));
		}
	}

	public static class Assay
	{
		public long assayID;
		public String uniqueID;
		public String schemaURI;
		public Annotation[] annotations;
	}

	private List<Assay> assays = new ArrayList<>();

	// ------------ public methods ------------
	
	public AssayDownload(String siteURL, String query)
	{
		this.siteURL = siteURL;
		this.query = query;
	}
	
	// make the request, parse out the result, and extract the content
	public void obtain() throws IOException
	{
		String url = siteURL + "/servlet/DownloadQuery?query=" + URLEncoder.encode(query, "UTF-8") + "&assays=true";
		
		try
		{
			HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			int cutoff = 300000; // 5 minutes
			conn.setConnectTimeout(cutoff);
			conn.setReadTimeout(cutoff);
			conn.connect();
			
			int respCode = conn.getResponseCode();
			if (respCode != 200) throw new IOException("HTTP response code " + respCode + " for URL [" + url + "]");
		
			// read the raw bytes into memory; abort if it's too long or too slow
			try (ZipInputStream zip = new ZipInputStream(new BufferedInputStream(conn.getInputStream())))
			{
				ZipEntry ze = zip.getNextEntry();
				while (ze != null)
				{
					String path = ze.getName(), name = new File(path).getName();
					if (path.endsWith(".json"))
					{
						JSONObject json = new JSONObject(new JSONTokener(zip));
						assays.add(unpackAssay(json));
					}
				
					zip.closeEntry();
					ze = zip.getNextEntry();
				}			

			}
		}
		catch (IOException ex) {throw new IOException("Failed on URL [" + url + "]", ex);}
	}
	
	public Assay[] getAssays() {return assays.toArray(new Assay[assays.size()]);}
	
	// ------------ private methods ------------
	
	private Assay unpackAssay(JSONObject json) throws JSONException
	{
		Assay assay = new Assay();
		
		assay.assayID = json.optLong("assayID", 0);
		assay.uniqueID = json.optString("uniqueID");
		assay.schemaURI = json.optString("schemaURI");
		
		JSONArray jsonList = json.optJSONArrayEmpty("annotations");
		assay.annotations = new Annotation[jsonList.length()];
		for (int n = 0; n < assay.annotations.length; n++)
		{
			Annotation annot = new Annotation();
			JSONObject jsonAnnot = jsonList.getJSONObject(n);
			annot.valueURI = jsonAnnot.optString("valueURI");
			annot.valueLabel = jsonAnnot.optString("valueLabel");
			annot.propURI = jsonAnnot.optString("propURI");
			annot.propLabel = jsonAnnot.optString("propLabel");
			annot.groupNest = jsonAnnot.optJSONArrayEmpty("groupNest").toStringArray();
			annot.groupLabel = jsonAnnot.optJSONArrayEmpty("groupLabel").toStringArray();
			assay.annotations[n] = annot;
		}
		
		return assay;
	}
}
