package eu.atos.seal.rm.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.util.StringUtils;

/**
 * Copyright © 2020  Atos Spain SA. All rights reserved.
This file is part of SEAL Request Manager (SEAL rm).
SEAL rm is free software: you can redistribute it and/or modify it under the terms of EUPL 1.2.
THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT ANY WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT, 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
DAMAGES OR OTHER LIABILITY, WHETHER IN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
See README file for the full disclaimer information and LICENSE file for full license information in the project root.

@author Atos Research and Innovation, Atos SPAIN SA
*/

public class DSClient
{
    private int index;

    private String displayName;

    private String logo;
    
    private List <AttributeClient> attrs;

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getLogo()
    {
        return logo;
    }

    public void setLogo(String logo)
    {
        this.logo = logo;
    }
    
    public List<AttributeClient> getAttrs()
    {
        return attrs;
    }

    public void setAttrs(List<AttributeClient> attrs)
    {
        this.attrs = new ArrayList<AttributeClient>();
        this.attrs = attrs;
    }

    // TODO What to show? *** ASK
    public static DSClient getDSClientFrom(DataSet dataset, int index) 
    {
        DSClient dsClient = new DSClient();
        dsClient.setIndex(index);
        dsClient.setDisplayName(dataset.getIssuerId() + " (Loa: " +
        						dataset.getLoa() + ")");
        dsClient.setLogo(dataset.getSubjectId());
        
        List<AttributeClient> auxAttrs = new ArrayList<AttributeClient>();
        int count = 0;
        for (AttributeType attr: dataset.getAttributes()) {
        	AttributeClient attrCl = new AttributeClient();
        	attrCl.setIndex(count++);
        	attrCl.setName(attr.getFriendlyName());
        	attrCl.setValue(StringUtils.arrayToCommaDelimitedString(attr.getValues().toArray()));
        	
        	auxAttrs.add(attrCl);
        }
        
        dsClient.setAttrs(auxAttrs);

        return dsClient;
    }
}
