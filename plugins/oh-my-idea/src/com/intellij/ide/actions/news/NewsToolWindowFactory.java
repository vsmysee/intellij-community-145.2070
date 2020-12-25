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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


public class NewsToolWindowFactory implements ToolWindowFactory {


  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {



    List<FutureTask<List<ItemInfo>>> tasks = new ArrayList<>();
    List<FutureTask<List<ItemInfo>>> bookTasks = new ArrayList<>();

    FutureTask<List<ItemInfo>> cnBlogs = new FutureTask<>(() -> {

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
      }
      catch (Exception e1) {
      }
      return itemInfoList;

    });
    tasks.add(cnBlogs);

    new Thread(cnBlogs).start();


    FutureTask<List<ItemInfo>> cnBlogsNews = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();


      try {


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

      return itemInfoList;

    });

    new Thread(cnBlogsNews).start();
    tasks.add(cnBlogsNews);


    FutureTask<List<ItemInfo>> hollischuang = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();


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

      return itemInfoList;

    });

    new Thread(hollischuang).start();
    tasks.add(hollischuang);


    FutureTask<List<ItemInfo>> oschina = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();


      try

      {
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

      catch (IOException e)

      {
        e.printStackTrace();
      }

      return itemInfoList;

    });

    new Thread(oschina).start();
    tasks.add(oschina);


    FutureTask<List<ItemInfo>> cto51 = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();

      try

      {

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

      catch (Exception e)

      {
        e.printStackTrace();
      }

      return itemInfoList;

    });

    tasks.add(cto51);

    new Thread(cto51).start();


    FutureTask<List<ItemInfo>> jdon = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();

      try

      {
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

      catch (IOException e)

      {
        e.printStackTrace();
      }

      return itemInfoList;

    });

    new Thread(jdon).start();

    tasks.add(jdon);


    FutureTask<List<ItemInfo>> toutiao = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();


      try

      {
        Document doc = Jsoup.connect("https://toutiao.io/").get();

        Elements items = doc.select("a[rel=external]");
        for (Element item : items) {
          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle(item.attr("title"));
          itemInfo.setDate("2012-12-25");
          itemInfoList.add(itemInfo);
        }
      }

      catch (IOException e)

      {
        e.printStackTrace();
      }

      return itemInfoList;

    });

    new Thread(toutiao).start();

    tasks.add(toutiao);


    FutureTask<List<ItemInfo>> thoughtworks = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();


      try

      {
        String from = "thoughtworks >";

        List<String> links = Arrays.asList("https://insights.thoughtworks.cn/tag/featured/", "https://insights.thoughtworks.cn/");
        for (String link : links) {

          Document doc = Jsoup.connect(link).get();

          Elements items = doc.select("a[rel=bookmark]");
          for (Element item : items) {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.setTitle(from + item.text());
            itemInfo.setDate("2012-12-25");
            itemInfoList.add(itemInfo);
          }
        }

      }

      catch (IOException e)

      {
        e.printStackTrace();
      }

      return itemInfoList;

    });

    new Thread(thoughtworks).start();
    tasks.add(thoughtworks);


    FutureTask<List<ItemInfo>> yueguang = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();


      try

      {
        String from = "yueguang >";

        List<String> links = Arrays.asList("https://www.williamlong.info/");
        for (String link : links) {

          Document doc = Jsoup.connect(link).get();

          Elements items = doc.select("a[rel=bookmark]");
          for (Element item : items) {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.setTitle(from + item.text());
            itemInfo.setDate("2012-12-25");
            itemInfoList.add(itemInfo);
          }
        }

      }

      catch (IOException e)

      {
        e.printStackTrace();
      }

      return itemInfoList;

    });

    new Thread(yueguang).start();
    tasks.add(yueguang);


    FutureTask<List<ItemInfo>> tuicool = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();


      try

      {
        Document doc = Jsoup.connect("https://www.tuicool.com/ah/20/").get();

        Elements items = doc.select("a[style=display: block]");
        for (Element item : items) {
          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle(item.text());
          itemInfo.setDate("2012-12-25");
          itemInfoList.add(itemInfo);
        }
      }

      catch (IOException e)

      {
        e.printStackTrace();
      }

      return itemInfoList;

    });

    new Thread(tuicool).start();
    tasks.add(tuicool);


    FutureTask<List<ItemInfo>> ibm = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();


      String from = "ibm >";
      try

      {
        Document doc = Jsoup.connect("https://developer.ibm.com/zh/articles/").get();

        Elements items = doc.select("h3.developer--card__title > span");
        for (Element item : items) {
          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle(from + item.text());
          itemInfo.setDate("2012-12-25");
          itemInfoList.add(itemInfo);
        }
      }

      catch (IOException e)

      {
        e.printStackTrace();
      }

      return itemInfoList;

    });

    new Thread(ibm).start();
    tasks.add(ibm);


    FutureTask<List<ItemInfo>> thenewstack = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();


      String from = "thenewstack >";
      try

      {

        List<String> links = Arrays.asList("https://thenewstack.io/","https://thenewstack.io/page/2","https://thenewstack.io/page/3" );
        for (String link : links) {
          Document doc = Jsoup.connect(link).get();
          Elements items = doc.select("h2.small > a");
          for (Element item : items) {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.setTitle(from + item.text());
            itemInfo.setDate("2012-12-25");
            itemInfoList.add(itemInfo);
          }
        }
      }

      catch (IOException e) {
        e.printStackTrace();
      }

      return itemInfoList;

    });

    new Thread(thenewstack).start();
    tasks.add(thenewstack);


    FutureTask<List<ItemInfo>> dockerone = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();


      String from = "dockerone >";
      try

      {

        List<String> links = Arrays.asList("http://dockerone.com/","http://dockerone.com/sort_type-new__day-0__is_recommend-0__page-2","http://dockerone.com/sort_type-new__day-0__is_recommend-0__page-3");
        for (String link : links) {
          Document doc = Jsoup.connect(link).get();
          Elements items = doc.select("h4 > a");
          for (Element item : items) {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.setTitle(from + item.text());
            itemInfo.setDate("2012-12-25");
            itemInfoList.add(itemInfo);
          }
        }
      }

      catch (IOException e) {
        e.printStackTrace();
      }

      return itemInfoList;

    });

    new Thread(dockerone).start();
    tasks.add(dockerone);


    FutureTask<List<ItemInfo>> lobste = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();


      String from = "lobste >";
      try

      {

        List<String> links = Arrays.asList("https://lobste.rs/");
        for (String link : links) {
          Document doc = Jsoup.connect(link).get();
          Elements items = doc.select("a[rel=ugc noreferrer]");
          for (Element item : items) {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.setTitle(from + item.text());
            itemInfo.setDate("2012-12-25");
            itemInfoList.add(itemInfo);
          }
        }
      }

      catch (IOException e) {
        e.printStackTrace();
      }

      return itemInfoList;

    });

    new Thread(lobste).start();
    tasks.add(lobste);


    FutureTask<List<ItemInfo>> ycombinator = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();


      String from = "ycombinator >";
      try

      {

        List<String> links = Arrays.asList("https://news.ycombinator.com/");
        for (String link : links) {
          Document doc = Jsoup.connect(link).get();
          Elements items = doc.select("a.storylink");
          for (Element item : items) {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.setTitle(from + item.text());
            itemInfo.setDate("2012-12-25");
            itemInfoList.add(itemInfo);
          }
        }
      }

      catch (IOException e) {
        e.printStackTrace();
      }

      return itemInfoList;

    });

    new Thread(ycombinator).start();
    tasks.add(ycombinator);


    FutureTask<List<ItemInfo>> tbooks = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();


      String from = "tbooks >";
      try

      {

        List<String> links = Arrays.asList("https://www.tuicool.com/books");
        for (String link : links) {
          Document doc = Jsoup.connect(link).get();
          Elements items = doc.select("div.title > a");
          for (Element item : items) {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.setTitle(from + item.text());
            itemInfo.setDate("2012-12-25");
            itemInfoList.add(itemInfo);
          }
        }
      }

      catch (IOException e) {
        e.printStackTrace();
      }

      return itemInfoList;

    });

    new Thread(tbooks).start();
    bookTasks.add(tbooks);



    FutureTask<List<ItemInfo>> tlbooks = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();


      String from = "tlbooks >";
      try

      {

        List<String> links = Arrays.asList("https://www.ituring.com.cn/book","https://www.ituring.com.cn/book?tab=book&sort=hot&page=1","https://www.ituring.com.cn/book?tab=book&sort=hot&page=2");
        for (String link : links) {
          Document doc = Jsoup.connect(link).get();
          Elements items = doc.select("h4.name > a");
          for (Element item : items) {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.setTitle(from + item.text());
            itemInfo.setDate("2012-12-25");
            itemInfoList.add(itemInfo);
          }
        }
      }

      catch (IOException e) {
        e.printStackTrace();
      }

      return itemInfoList;

    });

    new Thread(tlbooks).start();
    bookTasks.add(tlbooks);

    List<ItemInfo> itemInfoList = new ArrayList<>();
    List<ItemInfo> books = new ArrayList<>();

    try

    {

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


    for (FutureTask<List<ItemInfo>> task : tasks) {
      try {
        List<ItemInfo> itemInfos = task.get();
        itemInfoList.addAll(itemInfos);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
      catch (ExecutionException e) {
        e.printStackTrace();
      }
    }

    for (FutureTask<List<ItemInfo>> task : bookTasks) {
      try {
        List<ItemInfo> itemInfos = task.get();
        books.addAll(itemInfos);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
      catch (ExecutionException e) {
        e.printStackTrace();
      }
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

    TableView newsTable = getTableView();
    TableView bookTable = getTableView();



    final ListTableModel<ItemInfo> flatModel = new ListTableModel<>(new ColumnInfo[]{title, date}, itemInfoList);
    final ListTableModel<ItemInfo> bookModel = new ListTableModel<>(new ColumnInfo[]{title, date}, books);
    newsTable.setModelAndUpdateColumns(flatModel);
    bookTable.setModelAndUpdateColumns(bookModel);


    JScrollPane newTableScroll = ScrollPaneFactory.createScrollPane(newsTable, SideBorder.TOP);
    JScrollPane bookTableScroll = ScrollPaneFactory.createScrollPane(bookTable, SideBorder.TOP);


    toolWindow.getContentManager().addContent(ContentFactory.SERVICE.getInstance().createContent(newTableScroll, "资讯", false));
    toolWindow.getContentManager().addContent(ContentFactory.SERVICE.getInstance().createContent(bookTableScroll, "书讯", false));

  }

  @NotNull
  private TableView getTableView() {
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
    return myTable;
  }


}
