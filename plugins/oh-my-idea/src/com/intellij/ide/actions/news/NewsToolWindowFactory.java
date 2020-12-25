package com.intellij.ide.actions.news;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SideBorder;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.ListTableModel;
import jodd.http.HttpRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NewsToolWindowFactory implements ToolWindowFactory {


  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

    TableView myTable = new TableView<ItemInfo>();

    myTable.getEmptyText().setText("no news");
    myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    myTable.setShowHorizontalLines(false);
    myTable.setShowVerticalLines(false);
    myTable.setShowGrid(false);
    myTable.setIntercellSpacing(JBUI.emptySize());
    myTable.getColumnModel().setColumnSelectionAllowed(false);
    myTable.setRowHeight(25);
    myTable.setTableHeader(null);

    List<ItemInfo> itemInfoList = new ArrayList<>();


    try {
      List<Document> list = Arrays.asList(Jsoup.connect("https://www.cnblogs.com").get());

      for (Document doc : list) {


        Elements items = doc.select("a.post-item-title");
        for (Element item : items) {
          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle(item.text());
          itemInfo.setDate("2012-12-25");
          itemInfoList.add(itemInfo);
        }
      }


      List<String> links = Arrays.asList("https://news.cnblogs.com/", "https://news.cnblogs.com/n/page/2/");

      for (String link : links) {

        Document doc = Jsoup.connect(link).get();

        Elements items = doc.select("h2>a[target=_blank]");


        for (Element item : items) {
          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle(item.text());
          itemInfo.setDate("2012-12-25");
          itemInfoList.add(itemInfo);
        }
      }


    }
    catch (IOException e) {
      e.printStackTrace();
    }


    try {
      List<Document> list = Arrays
        .asList(Jsoup.connect("https://www.hollischuang.com/page/1").get(), Jsoup.connect("https://www.hollischuang.com/page/2").get(),
                Jsoup.connect("https://www.hollischuang.com/page/3").get(), Jsoup.connect("https://www.hollischuang.com/page/4").get());

      for (Document doc : list) {


        Elements items = doc.select("h2 > a[title]");
        for (Element item : items) {
          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle(item.attr("title"));
          itemInfo.setDate("2012-12-25");
          itemInfoList.add(itemInfo);
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }


    try {
      List<Document> list = Arrays
        .asList(Jsoup.connect("https://www.oschina.net/news/widgets/_news_index_generic_list?p=1&type=ajax").get(),
                Jsoup.connect("https://www.oschina.net/news/widgets/_news_index_generic_list?p=2&type=ajax").get(),
                Jsoup.connect("https://www.oschina.net/news/widgets/_news_index_generic_list?p=3&type=ajax").get(),
                Jsoup.connect("https://www.oschina.net/news/widgets/_news_index_generic_list?p=4&type=ajax").get());
      String from = "oschina >";

      for (Document doc : list) {


        Elements items = doc.select("h3 > a");
        for (Element item : items) {
          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle(from + item.attr("title"));
          itemInfo.setDate("2012-12-25");
          itemInfoList.add(itemInfo);
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    try {

      List<String> list = Arrays.asList(HttpRequest.get(
        "https://www.51cto.com/api/v1/index.php?c=index&a=articleFeed&sign=11111&timestamp=1&tag=translation&limit=10&page=1").send()
                                          .bodyText(), HttpRequest.get(
        "https://www.51cto.com/api/v1/index.php?c=index&a=articleFeed&sign=11111&timestamp=1&tag=translation&limit=10&page=2").send()
                                          .bodyText(), HttpRequest.get(
        "https://www.51cto.com/api/v1/index.php?c=index&a=articleFeed&sign=11111&timestamp=1&tag=translation&limit=10&page=3").send()
                                          .bodyText());


      String from = "51cto >";


      for (String apiRes : list) {


        JSONArray array = new JSONArray(apiRes);
        for (int i = 0; i < array.length(); i++) {


          JSONObject jo = array.getJSONObject(i);

          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle(from + jo.getString("title"));
          itemInfo.setDate("2012-12-25");
          itemInfoList.add(itemInfo);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }


    try {
      Document doc = Jsoup.connect("https://www.jdon.com/").get();

      Elements important = doc.select("div.important");

      String from = "jdon >";

      Element first = important.first();

      Elements items = first.select("a");

      for (Element item : items) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.setTitle(from + item.text());
        itemInfo.setDate("2012-12-25");
        itemInfoList.add(itemInfo);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }


    try {
      Document doc = Jsoup.connect("https://toutiao.io/").get();

      Elements items = doc.select("a[rel=external]");
      for (Element item : items) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.setTitle(item.attr("title"));
        itemInfo.setDate("2012-12-25");
        itemInfoList.add(itemInfo);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    try {
      Document doc = Jsoup.connect("https://insights.thoughtworks.cn/").get();
      String from = "thoughtworks >";

      Elements items = doc.select("a[rel=bookmark]");
      for (Element item : items) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.setTitle(from + item.text());
        itemInfo.setDate("2012-12-25");
        itemInfoList.add(itemInfo);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }


    try {
      Document doc = Jsoup.connect("https://www.tuicool.com/ah/20/").get();

      Elements items = doc.select("a[style=display: block]");
      for (Element item : items) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.setTitle(item.text());
        itemInfo.setDate("2012-12-25");
        itemInfoList.add(itemInfo);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }


    try {

      String apiRes = HttpRequest.post("https://s.geekbang.org/api/gksearch/search").header("User-Agent",
                                                                                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36")
        .header("Referer", "https://s.geekbang.org/search/c=0/k=android/t=").header("Content-Type", "application/json;charset=UTF-8")
        .body("{\"q\":\"android\",\"t\":0,\"s\":20,\"p\":1}").send().bodyText();


    }
    catch (Exception e) {
      e.printStackTrace();
    }


    try {

      URL url = new URL("https://api.readhub.cn/technews?lastCursor=@null&pageSize=20");
      HttpURLConnection con = (HttpURLConnection)url.openConnection();

      con.setRequestMethod("GET");
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("Accept", "application/json");


      BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line + "\n");
      }
      br.close();

      String apiRes = sb.toString();
      JSONObject jo = new JSONObject(apiRes);
      JSONArray data = jo.getJSONArray("data");
      for (int i = 0; i < data.length(); i++) {
        JSONObject o = (JSONObject)data.get(i);

        ItemInfo itemInfo = new ItemInfo();
        itemInfo.setTitle("redhub >" + o.getString("title"));
        itemInfo.setDate(o.getString("publishDate"));
        itemInfoList.add(itemInfo);

      }

    }
    catch (Exception e) {
      e.printStackTrace();
    }


    ColumnInfo<ItemInfo, String> title = new ColumnInfo<ItemInfo, String>("title") {
      @Nullable
      @Override
      public String valueOf(ItemInfo item) {
        return item.getTitle();
      }
    };

    ColumnInfo<ItemInfo, String> date = new ColumnInfo<ItemInfo, String>("date") {
      @Nullable
      @Override
      public String valueOf(ItemInfo item) {
        return item.getDate();
      }

      @Override
      public int getWidth(JTable table) {
        return 100;
      }
    };

    final ListTableModel<ItemInfo> flatModel = new ListTableModel<>(new ColumnInfo[]{title, date}, itemInfoList);
    myTable.setModelAndUpdateColumns(flatModel);


    JScrollPane tableScroll = ScrollPaneFactory.createScrollPane(myTable, SideBorder.TOP);


    toolWindow.getContentManager().addContent(ContentFactory.SERVICE.getInstance().createContent(tableScroll, "资讯", false));

  }


}
