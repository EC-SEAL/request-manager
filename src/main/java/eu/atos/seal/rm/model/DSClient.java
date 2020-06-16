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

public class DSClient
{
    private int index;

    private String displayName;

    private String logo;

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

    // TODO What to show? *** ASK
    public static DSClient getDSClientFrom(DataSet dataset, int index) 
    {
        DSClient dsClient = new DSClient();
        dsClient.setIndex(index);
        dsClient.setDisplayName(dataset.getIssuerId());
        dsClient.setLogo(dataset.getSubjectId());

        return dsClient;
    }
}
