package eu.atos.seal.rm.model;
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

import java.util.ArrayList;
import java.util.List;

public class AttributeSetClient
{
    private String id;
    
    private String source;
    private String issuer;
    private String type;
    private String isLoa;   // label LoA or LLoA
    private String loa;		// the value

    private List<AttributeClient> attributeClientList;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }
    
    public String getIssuer()
    {
        return issuer;
    }

    public void setIssuer(String issuer)
    {
        this.issuer = issuer;
    }
    
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
    
    public String getIsLoa()
    {
        return isLoa;
    }

    public void setIsLoa(String isLoa)
    {
        this.isLoa = isLoa;
    }
    
    public String getLoa()
    {
        return loa;
    }

    public void setLoa(String loa)
    {
        this.loa = loa;
    }

    public List<AttributeClient> getAttributeClientList()
    {
        return attributeClientList;
    }

    public void setAttributeClientList(List<AttributeClient> attributeClientList)
    {
        this.attributeClientList = attributeClientList;
    }

    public static AttributeSetClient getAttributeSetClientFrom(AttributeSet attributeSet)
    {
        AttributeSetClient attributeSetClient = new AttributeSetClient();
        attributeSetClient.setId(attributeSet.getId());

        List<AttributeClient> attributeClientList = new ArrayList<AttributeClient>();
        int index = 0;
        if (attributeSet.getAttributes() != null)
        {
            for (AttributeType attributeType : attributeSet.getAttributes())
            {
                attributeClientList
                        .add(AttributeClient.getAttributeClientFrom(attributeType, index));
                index++;
            }
        }
        attributeSetClient.setAttributeClientList(attributeClientList);

        return attributeSetClient;
    }
}
