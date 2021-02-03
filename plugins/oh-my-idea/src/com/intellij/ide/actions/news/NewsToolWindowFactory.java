package com.intellij.ide.actions.news;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.FutureTask;


public class NewsToolWindowFactory implements ToolWindowFactory {

  interface GetContent {

    void run(List<ItemInfo> list) throws Exception;

  }

  interface ItemProcessor {
    String get(Element e);
  }

  private FutureTask<List<ItemInfo>> buildTask(GetContent runnable) {

    return new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();

      try {
        runnable.run(itemInfoList);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      return itemInfoList;

    });
  }


  private GetContent buildGetRequest(String key, String url, String select, ItemProcessor itemProcessor) {
    return list -> {

      Document document = Jsoup.connect(url).get();

      Elements items = document.select(select);
      for (Element item : items) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.setTitle(key + " >" + itemProcessor.get(item));
        itemInfo.setDate("...");
        list.add(itemInfo);
      }

    };
  }


  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

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
        return 150;
      }
    };

    TableView newsTable = getTableView();

    List<FutureTask<List<ItemInfo>>> newsTasks = new ArrayList<>();

    newsTasks.add(buildTask(buildGetRequest("trend", "https://awesomeopensource.com/projects/", "div.aos_project_title > a", e -> e.text())));

    newsTasks.add(buildTask(buildGetRequest("36kr", "https://www.36kr.com", "a.article-item-title", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("ifanr", "https://www.ifanr.com/", "a.js-title-transform", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("iheima", "http://www.iheima.com", "a.title", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("donews", "https://www.donews.com/", "div.content > span.title", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("cnbeta", "https://www.cnbeta.com/", "div.item > dl > dt > a", e -> e.text())));


    newsTasks.add(buildTask(buildGetRequest("toutiao", "https://toutiao.io/posts/hot/7", "h3.title > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("jishuin", "https://jishuin.proginn.com/", "div.article-title > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("jishuin", "https://jishuin.proginn.com/default/2", "div.article-title > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("jishuin", "https://jishuin.proginn.com/default/3", "div.article-title > a", e -> e.text())));


    newsTasks.add(buildTask(buildGetRequest("cbdio", "http://www.cbdio.com/node_2570.htm", "p.cb-media-title > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("woshipm", "http://www.woshipm.com/", "h2.post-title > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("myzaker", "https://www.myzaker.com", "h2.article-title", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("topjava", "https://www.topjavablogs.com/", "a.itemLink", e -> e.text())));

    newsTasks.add(buildTask(buildGetRequest("amazonaws", "https://amazonaws-china.com/cn/blogs/china/", "h2.blog-post-title > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("amazonaws", "https://amazonaws-china.com/cn/blogs/china/page/2/", "h2.blog-post-title > a", e -> e.text())));

    newsTasks.add(buildTask(buildGetRequest("huanqiukexue", "https://huanqiukexue.com/plus/list.php?tid=1", "div.astrtext > a >h4", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("huanqiukexue", "https://huanqiukexue.com/plus/list.php?tid=1&TotalResult=4849&PageNo=2", "div.astrtext > a >h4", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("huanqiukexue", "https://huanqiukexue.com/plus/list.php?tid=1&TotalResult=4849&PageNo=3", "div.astrtext > a >h4", e -> e.text())));



    newsTasks.add(buildTask(buildGetRequest("thenewstack", "https://thenewstack.io/", "h2.small > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("thenewstack", "https://thenewstack.io/page/2", "h2.small > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("thenewstack", "https://thenewstack.io/page/3", "h2.small > a", e -> e.text())));


    newsTasks.add(buildTask(buildGetRequest("dockerone", "http://dockerone.com/", "h4 > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("dockerone", "http://dockerone.com/sort_type-new__day-0__is_recommend-0__page-2", "h4 > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("dockerone", "http://dockerone.com/sort_type-new__day-0__is_recommend-0__page-3", "h4 > a", e -> e.text())));


    newsTasks.add(buildTask(buildGetRequest("meituan", "https://tech.meituan.com/", "h2.post-title > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("meituan", "https://tech.meituan.com/page/2.html", "h2.post-title > a", e -> e.text())));

    newsTasks.add(buildTask(buildGetRequest("kejilie", "http://www.kejilie.com", "h3.am_list_title > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("tmtpost", "https://www.tmtpost.com/lists/latest_list_new", "li.part_post > div.info > a > h3", e -> e.text())));

    newsTasks.add(buildTask(buildGetRequest("infoworld", "https://www.infoworld.com", "div.post-cont > h3 > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("dzone", "https://dzone.com/", "h3.article-title > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("geeksforgeeks", "https://www.geeksforgeeks.org/", "div.content > div.head > a", e -> e.text())));

    newsTasks.add(buildTask(buildGetRequest("segmentfault", "https://segmentfault.com/news/", "h4.news__item-title", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("cnblogs", "https://www.cnblogs.com", "a.post-item-title", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("ithome", "https://it.ithome.com", "ul.bl > li > a.img > img", e -> e.attr("alt"))));
    newsTasks.add(buildTask(buildGetRequest("donews", "https://www.donews.com/", "div.info > p.title", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("cnblogs", "https://news.cnblogs.com/", "h2 > a[target=_blank]", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("cnblogs", "https://news.cnblogs.com/n/page/2/", "h2 > a[target=_blank]", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("hollischuang", "https://www.hollischuang.com/page/1", "h2 > a[title]", e -> e.attr("title"))));
    newsTasks.add(buildTask(buildGetRequest("hollischuang", "https://www.hollischuang.com/page/2", "h2 > a[title]", e -> e.attr("title"))));
    newsTasks.add(buildTask(buildGetRequest("hollischuang", "https://www.hollischuang.com/page/3", "h2 > a[title]", e -> e.attr("title"))));
    newsTasks.add(buildTask(buildGetRequest("hollischuang", "https://www.hollischuang.com/page/4", "h2 > a[title]", e -> e.attr("title"))));
    newsTasks.add(buildTask(buildGetRequest("toutiao", "https://toutiao.io/", "a[rel=external]", e -> e.attr("title"))));
    newsTasks.add(buildTask(buildGetRequest("yueguang", "https://www.williamlong.info/", "a[rel=bookmark]", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("tuicool", "https://www.tuicool.com/ah/20/", "a[style=display: block]", e -> e.text())));
    newsTasks
      .add(buildTask(buildGetRequest("ibm", "https://developer.ibm.com/zh/articles/", "h3.developer--card__title > span", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("lobste", "https://lobste.rs/", "a[rel=ugc noreferrer]", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("ycombinator", "https://news.ycombinator.com/", "a.storylink", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("afoo", "https://afoo.me/posts.html", "header.title > div > h2 > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("qbitai", "https://www.qbitai.com/", "div.text_box > h4 > a", e -> e.text())));

    newsTasks.add(buildTask(buildGetRequest("ruanyifeng", "http://www.ruanyifeng.com/blog/archives.html", "#alpha-inner > div.module-categories > div.module-content > ul.module-list > li > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("yinwang", "http://www.yinwang.org", "li.list-group-item > a", e -> e.text())));

    newsTasks.add(buildTask(buildGetRequest("oschina", "https://www.oschina.net/translate", "div.translate-item > div.content > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("oschina", "https://www.oschina.net/translate/widgets/_translate_index_list?category=0&tab=completed&sort=&p=2&type=ajax", "div.translate-item > div.content > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("oschina", "https://www.oschina.net/translate/widgets/_translate_index_list?category=0&tab=completed&sort=&p=3&type=ajax", "div.translate-item > div.content > a", e -> e.text())));

    newsTasks.add(buildTask(buildGetRequest("jdon", "https://www.jdon.com/", "div.important > div.info > a", e -> e.text())));

    newsTasks.add(buildTask(buildGetRequest("cncf", "https://www.cncf.io/blog/", "p.archive-title > a", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("cncf", "https://www.cncf.io/blog/page/2/", "p.archive-title > a", e -> e.text())));


    newsTasks
      .add(buildTask(buildGetRequest("thoughtworks", "https://insights.thoughtworks.cn/tag/featured/", "a[rel=bookmark]", e -> e.text())));
    newsTasks.add(buildTask(buildGetRequest("thoughtworks", "https://insights.thoughtworks.cn/", "a[rel=bookmark]", e -> e.text())));

    for (int i = 1; i <= 4; i++) {
      newsTasks.add(buildTask(
        buildGetRequest("oschina", "https://www.oschina.net/news/widgets/_news_index_generic_list?p=" + i + "&type=ajax", "h3 > a",
                        e -> e.attr("title"))));
    }

    newsTasks.add(buildTask(itemInfoList -> {
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

    }));



    newsTasks.add(buildTask(itemInfoList -> {
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

    }));


    newsTasks.add(buildTask(itemInfoList -> {

      String from = "oschina >";

      for (String link : Arrays
        .asList("https://my.oschina.net/editorial-story/widgets/_space_index_newest_blog?catalogId=0&q=&p=1&type=ajax",
                "https://my.oschina.net/editorial-story/widgets/_space_index_newest_blog?catalogId=0&q=&p=2&type=ajax",
                "https://my.oschina.net/editorial-story/widgets/_space_index_newest_blog?catalogId=0&q=&p=3&type=ajax")) {
        Document doc = Jsoup.connect(link).get();

        Elements select = doc.select("div.blog-item > div.content");

        for (Element item : select) {

          Elements items = item.select("a");

          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle(from + items.first().text());
          itemInfo.setDate("");
          itemInfoList.add(itemInfo);
        }
      }

    }));


    newsTasks.add(buildTask(itemInfoList -> {

      String from = "manateelazycat >";

      List<String> links2 = Arrays.asList("https://manateelazycat.github.io/index.html");
      for (String link : links2) {
        Document doc = Jsoup.connect(link).get();

        Elements select = doc.select("li.post-line");

        for (Element item : select) {

          Elements items = item.select("a.post-title");
          Elements dates = item.select("div.post-date");

          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle(from + items.first().text());
          itemInfo.setDate(dates.first().text());
          itemInfoList.add(itemInfo);
        }
      }

    }));


    newsTasks.add(buildTask(itemInfoList -> {
      String from = "jetbrains >";

      List<String> links2 = Arrays.asList("https://blog.jetbrains.com/idea/category/releases/");
      for (String link : links2) {
        Document doc = Jsoup.connect(link).get();

        Elements select = doc.select("article");

        for (Element item : select) {

          Elements items = item.select("h3");
          Elements dates = item.select("time");

          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle(from + items.first().text());
          itemInfo.setDate(dates.first().text());
          itemInfoList.add(itemInfo);
        }
      }

    }));

    newsTasks.add(buildTask(itemInfoList -> {
      String from = "spring >";

      List<String> links2 = Arrays.asList("https://spring.io/blog/category/releases");
      for (String link : links2) {
        Document doc = Jsoup.connect(link).get();

        Elements select = doc.select("article.blog--container");

        for (Element item : select) {

          Elements items = item.select("h2.blog--title > a");
          Elements dates = item.select("time");

          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle(from + items.first().text());
          itemInfo.setDate(dates.first().text());
          itemInfoList.add(itemInfo);
        }
      }

    }));


    newsTasks.add(buildTask(itemInfoList -> {
      String from = "itpub >";

      List<String> links2 = Arrays.asList("https://z.itpub.net/");
      for (String link : links2) {
        Document doc = Jsoup.connect(link).get();

        Elements select = doc.select("li.has-img");

        for (Element item : select) {

          Elements items = item.select("h4");
          Elements dates = item.select("span.time");

          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle(from + items.first().text());
          itemInfo.setDate(dates.first().text());
          itemInfoList.add(itemInfo);
        }

      }

    }));


    runTasks(title, date, newsTable, newsTasks, "Loading");
    JScrollPane newTableScroll = ScrollPaneFactory.createScrollPane(newsTable, SideBorder.TOP);
    toolWindow.getContentManager().addContent(ContentFactory.SERVICE.getInstance().createContent(newTableScroll, "资讯", false));

  }

  private void runTasks(final ColumnInfo<ItemInfo, String> title,
                        final ColumnInfo<ItemInfo, String> date,
                        final TableView newsTable,
                        final List<FutureTask<List<ItemInfo>>> tasks,
                        String desc) {

    Task.Backgroundable backNewTask = new Task.Backgroundable(null, desc, true) {
      @Override
      public void run(@NotNull final ProgressIndicator indicator) {
        List<ItemInfo> allItems = new ArrayList<>();

        for (FutureTask<List<ItemInfo>> task : tasks) {
          new Thread(task).run();
        }

        for (FutureTask<List<ItemInfo>> task : tasks) {
          try {
            allItems.addAll(task.get());
          }
          catch (Exception e) {
            e.printStackTrace();
          }
        }

        ApplicationManager.getApplication().invokeLater(() -> {
          newsTable.setModelAndUpdateColumns(new ListTableModel<>(new ColumnInfo[]{title, date}, allItems));
        });
      }
    };

    ProgressManager.getInstance().run(backNewTask);

  }


  @NotNull
  private TableView getTableView() {
    TableView myTable = new TableView<ItemInfo>();

    myTable.getEmptyText().setText("loading......");
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
