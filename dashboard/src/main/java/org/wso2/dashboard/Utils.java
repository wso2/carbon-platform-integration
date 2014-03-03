/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.dashboard;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Properties;

public class Utils {
    public static String getDataArray(JSONArray ja) {
        StringBuffer s = new StringBuffer();
        s.append("[");
        if (ja != null && ja.size() > 0) {
            Iterator i = ja.iterator();
            while (i.hasNext()) {
                JSONObject jo = (JSONObject) i.next();
                s.append("[") ;
                s.append(jo.getString("id"));
                s.append("," );
                s.append(jo.getString(Constant.PASS));
                s.append("," );
                s.append(jo.getString(Constant.FAIL));
                s.append("," );
                s.append(jo.getString(Constant.SKIP));
                s.append( "],");
            }
        }
        s.append("]");
        return s.toString();
    }

    public static String getDataAsAList(JSONArray ja, String key) {
        StringBuffer s = new StringBuffer();
        s.append("");
        if (ja != null && ja.size() > 0) {
            Iterator i = ja.iterator();
            int count = 0;
            while (i.hasNext()) {
                JSONObject jo = (JSONObject) i.next();
                if (jo.containsKey(key)) {
                    s.append(jo.getString(key));
                    s.append(",");
                }
                if(count == 5) {
                    break;
                }
                count++;

            }
        }
        return s.toString();
    }

    public static String getPieChartData(JSONObject jo) {
        String s = "";
        if (jo != null && jo.has(Constant.PASS)) {
            s = s + "['Pass'," + jo.getString(Constant.PASS) + "],";
            s = s + "['Fail'," + jo.getString(Constant.FAIL) + "],";
            s = s + "['Skip'," + jo.getString(Constant.SKIP) + "]";
        }
        return s;
    }
    public static double round(double value) {
        DecimalFormat twoDForm = new DecimalFormat("#.###");
        return Double.parseDouble(twoDForm.format(value));
    }

   public static String getProperty(String key) {
            Properties dbProps = new Properties();
            InputStream is = null;

            try {
                is = DataProvider.class.getClassLoader().getResourceAsStream("dbconf.properties");
                dbProps.load(is);//this may throw IOException
            } catch (IOException e) {

                System.err.println("Database configuration not found");
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }

                } catch (IOException e) {

                }
            }
            return dbProps.getProperty(key);
        }
}
