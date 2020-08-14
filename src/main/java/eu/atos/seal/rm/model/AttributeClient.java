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

package eu.atos.seal.rm.model;
import org.springframework.util.StringUtils;

public class AttributeClient
{
    private int index;

    private String name;

    private String value;

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public static AttributeClient getAttributeClientFrom(AttributeType attributeType, int index)
    {
        AttributeClient attributeClient = new AttributeClient();
        attributeClient.setIndex(index);
        attributeClient.setName((attributeType.getFriendlyName() != null)
                ? attributeType.getFriendlyName() : attributeType.getName());
        try
        {
            attributeClient.setValue(
                    StringUtils.arrayToCommaDelimitedString(attributeType.getValues().toArray()));
        }
        catch (NullPointerException e)
        {
        }

        return attributeClient;
    }
}
