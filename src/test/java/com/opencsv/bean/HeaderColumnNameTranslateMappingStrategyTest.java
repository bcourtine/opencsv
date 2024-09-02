package com.opencsv.bean;

/*
 Copyright 2007 Kyle Miller.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import com.opencsv.bean.mocks.MockBean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HeaderColumnNameTranslateMappingStrategyTest {

   @Test
   public void testParse() {
      String s = "n,o,foo\n" +
            "kyle,123456,emp123\n" +
            "jimmy,abcnum,cust09878";
      HeaderColumnNameTranslateMappingStrategy<MockBean> strat = new HeaderColumnNameTranslateMappingStrategy<>();
      strat.setType(MockBean.class);
      Map<String, String> map = new HashMap<>();
      map.put("n", "name");
      map.put("o", "orderNumber");
      map.put("foo", "id");
      strat.setColumnMapping(map);

      CsvToBean<MockBean> csv = new CsvToBeanBuilder<MockBean>(new StringReader(s))
              .withMappingStrategy(strat).build();
      List<MockBean> list = csv.parse();
      assertNotNull(list);
      assertEquals(2, list.size());
      MockBean bean = list.get(0);
      assertEquals("kyle", bean.getName());
      assertEquals("123456", bean.getOrderNumber());
      assertEquals("emp123", bean.getId());
   }

   @Test
   @DisplayName("parse the csv file with only a subset of columns and fields.")
   public void testParseWithSubset() {
      String s = "n,o,foo\n" +
              "kyle,123456,emp123\n" +
              "jimmy,abcnum,cust09878";
      HeaderColumnNameTranslateMappingStrategy<MockBean> strat = new HeaderColumnNameTranslateMappingStrategyBuilder<MockBean>()
              .withType(MockBean.class)
              .build();
      Map<String, String> map = new HashMap<>();
      map.put("n", "name");
      map.put("o", "orderNumber");
      strat.setColumnMapping(map);

      CsvToBean<MockBean> csv = new CsvToBeanBuilder<MockBean>(new StringReader(s))
              .withMappingStrategy(strat).build();
      List<MockBean> list = csv.parse();
      assertNotNull(list);
      assertEquals(2, list.size());
      MockBean bean = list.get(0);
      assertEquals("kyle", bean.getName());
      assertEquals("123456", bean.getOrderNumber());
      assertNull(bean.getId());
   }

    @Test
    @DisplayName("Show that even if there are column field name matches they will not be populated if not in the name translate map.")
    public void onlyConvertWhatIsInTheMap() {
        String s = "n,o,foo,name,id,orderNumber,num,doubleNum\n" +
                "kyle,123456,emp123,aName,aId,aOrderNumber,22,3.14\n" +
                "jimmy,abcnum,cust09878,bName,bId,bOrderNumber,44,8.3";
        HeaderColumnNameTranslateMappingStrategy<MockBean> strat = new HeaderColumnNameTranslateMappingStrategyBuilder<MockBean>()
                .withForceCorrectRecordLength(true)
                .build();
        strat.setType(MockBean.class);
        Map<String, String> map = new HashMap<>();
        map.put("n", "name");
        map.put("o", "orderNumber");
        strat.setColumnMapping(map);

        CsvToBean<MockBean> csv = new CsvToBeanBuilder<MockBean>(new StringReader(s))
                .withMappingStrategy(strat).build();
        List<MockBean> list = csv.parse();
        assertNotNull(list);
        assertEquals(2, list.size());
        MockBean bean = list.get(0);
        assertEquals("kyle", bean.getName());
        assertEquals("123456", bean.getOrderNumber());
        assertNull(bean.getId());
        assertEquals(0, bean.getNum());
        assertEquals(0.0, bean.getDoubleNum(), 0.01);
    }

   @Test
   public void getColumnNameReturnsNullIfColumnNumberIsTooLarge() {
      String s = "n,o,foo\n" +
            "kyle,123456,emp123\n" +
            "jimmy,abcnum,cust09878";
      HeaderColumnNameTranslateMappingStrategy<MockBean> strat = new HeaderColumnNameTranslateMappingStrategy<>();
      strat.setType(MockBean.class);
      Map<String, String> map = new HashMap<>();
      map.put("n", "name");
      map.put("o", "orderNumber");
      map.put("foo", "id");
      strat.setColumnMapping(map);

      CsvToBean<MockBean> csv = new CsvToBeanBuilder<MockBean>(new StringReader(s))
              .withMappingStrategy(strat).build();
      csv.parse();

      assertEquals("name", strat.getColumnName(0));
      assertEquals("orderNumber", strat.getColumnName(1));
      assertEquals("id", strat.getColumnName(2));
      assertNull(strat.getColumnName(3));
   }

   @Test
   public void columnNameMappingShouldBeCaseInsensitive() {
      String s = "n,o,Foo\n" +
            "kyle,123456,emp123\n" +
            "jimmy,abcnum,cust09878";
      HeaderColumnNameTranslateMappingStrategy<MockBean> strat = new HeaderColumnNameTranslateMappingStrategy<>();
      strat.setType(MockBean.class);
      Map<String, String> map = new HashMap<>();
      map.put("n", "name");
      map.put("o", "orderNumber");
      map.put("foo", "id");
      strat.setColumnMapping(map);
      assertNotNull(strat.getColumnMapping());

      CsvToBean<MockBean> csv = new CsvToBeanBuilder<MockBean>(new StringReader(s))
              .withMappingStrategy(strat).build();
      csv.parse();

      assertEquals("name", strat.getColumnName(0));
      assertEquals("orderNumber", strat.getColumnName(1));
      assertEquals("id", strat.getColumnName(2));
      assertNull(strat.getColumnName(3));
   }
}
