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

  interface GetContent {

    void run(List<ItemInfo> list) throws Exception;

  }

  private FutureTask<List<ItemInfo>> buildTask(GetContent runnable) {
    FutureTask<List<ItemInfo>> task = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();

      try {
        runnable.run(itemInfoList);
      }
      catch (Exception e) {
      }
      return itemInfoList;

    });
    new Thread(task).start();

    return task;
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
        return 100;
      }
    };

    TableView newsTable = getTableView();
    TableView bookTable = getTableView();
    TableView blogTable = getTableView();


    List<FutureTask<List<ItemInfo>>> tasks = new ArrayList<>();


    tasks.add(buildTask(list -> {

      for (Document doc : Arrays.asList(Jsoup.connect("https://www.cnblogs.com").get())) {

        Elements items = doc.select("a.post-item-title");
        for (Element item : items) {
          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle("cnblogs>" + item.text());
          itemInfo.setDate("2012-12-25");
          list.add(itemInfo);
        }
      }
    }));


    tasks.add(buildTask(list -> {

      for (Document doc : Arrays.asList(Jsoup.connect("https://it.ithome.com").get())) {

        Elements items = doc.select("ul.bl > li > a.img > img");
        for (Element item : items) {
          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle("ithome>" + item.attr("alt"));
          itemInfo.setDate("2012-12-25");
          list.add(itemInfo);
        }
      }
    }));


    tasks.add(buildTask(list -> {

      for (Document doc : Arrays.asList(Jsoup.connect("https://www.donews.com/").get())) {

        Elements items = doc.select("div.info > p.title");
        for (Element item : items) {
          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle("donews>" + item.text());
          itemInfo.setDate("2012-12-25");
          list.add(itemInfo);
        }
      }
    }));


    tasks.add(buildTask(list -> {

      List<String> links = Arrays.asList("https://news.cnblogs.com/", "https://news.cnblogs.com/n/page/2/");

      for (String link : links) {

        Document doc = Jsoup.connect(link).get();
        Elements items = doc.select("h2>a[target=_blank]");

        for (Element item : items) {
          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle("cnblogs>" + item.text());
          itemInfo.setDate("2012-12-25");
          list.add(itemInfo);
        }
      }

    }));


    tasks.add(buildTask(list -> {

      List<Document> lists = Arrays
        .asList(Jsoup.connect("https://www.hollischuang.com/page/1").get(), Jsoup.connect("https://www.hollischuang.com/page/2").get(),
                Jsoup.connect("https://www.hollischuang.com/page/3").get(), Jsoup.connect("https://www.hollischuang.com/page/4").get());

      for (Document doc : lists) {
        Elements items = doc.select("h2 > a[title]");
        for (Element item : items) {
          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle("hollischuang>" + item.attr("title"));
          itemInfo.setDate("2012-12-25");
          list.add(itemInfo);
        }
      }

    }));


    tasks.add(buildTask(list -> {

      List<Document> lists = Arrays
        .asList(Jsoup.connect("https://www.oschina.net/news/widgets/_news_index_generic_list?p=1&type=ajax").get(),
                Jsoup.connect("https://www.oschina.net/news/widgets/_news_index_generic_list?p=2&type=ajax").get(),
                Jsoup.connect("https://www.oschina.net/news/widgets/_news_index_generic_list?p=3&type=ajax").get(),
                Jsoup.connect("https://www.oschina.net/news/widgets/_news_index_generic_list?p=4&type=ajax").get());
      String from = "oschina >";

      for (Document doc : lists) {
        Elements items = doc.select("h3 > a");
        for (Element item : items) {
          ItemInfo itemInfo = new ItemInfo();
          itemInfo.setTitle(from + item.attr("title"));
          itemInfo.setDate("2012-12-25");
          list.add(itemInfo);
        }
      }

    }));


    FutureTask<List<ItemInfo>> cto51 = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();

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

      return itemInfoList;

    });

    tasks.add(cto51);
    new Thread(cto51).start();


    FutureTask<List<ItemInfo>> jdon = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();

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

      return itemInfoList;

    });

    new Thread(jdon).start();
    tasks.add(jdon);


    FutureTask<List<ItemInfo>> toutiao = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();

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

        List<String> links = Arrays.asList("https://thenewstack.io/", "https://thenewstack.io/page/2", "https://thenewstack.io/page/3");
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

    // new Thread(thenewstack).start();
    //tasks.add(thenewstack);


    FutureTask<List<ItemInfo>> dockerone = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();

      String from = "dockerone >";
      try

      {

        List<String> links = Arrays.asList("http://dockerone.com/", "http://dockerone.com/sort_type-new__day-0__is_recommend-0__page-2",
                                           "http://dockerone.com/sort_type-new__day-0__is_recommend-0__page-3");
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

    //new Thread(ycombinator).start();
    //tasks.add(ycombinator);


    Task.Backgroundable backBookTask = new Task.Backgroundable(null, "Loading book", true) {
      @Override
      public void run(@NotNull final ProgressIndicator indicator) {
        List<ItemInfo> itemInfoList = new ArrayList<>();

        try

        {
          {
            String from = "tlbooks >";

            List<String> links = Arrays
              .asList("https://www.ituring.com.cn/book", "https://www.ituring.com.cn/book?tab=book&sort=hot&page=1",
                      "https://www.ituring.com.cn/book?tab=book&sort=hot&page=2");
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


          {
            String from = "tbooks >";

            List<String> links2 = Arrays.asList("https://www.tuicool.com/books");
            for (String link : links2) {
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

          {
            String from = "ebooks >";

            List<String> links2 = Arrays.asList("https://it-ebooks.info");
            for (String link : links2) {
              Document doc = Jsoup.connect(link).get();
              Elements items = doc.select("div.top_box > a");
              for (Element item : items) {
                ItemInfo itemInfo = new ItemInfo();
                itemInfo.setTitle(from + item.text());
                itemInfo.setDate("2012-12-25");
                itemInfoList.add(itemInfo);
              }


            }

          }
        }

        catch (Exception e) {
          e.printStackTrace();
        }


        ApplicationManager.getApplication().invokeLater(new Runnable() {
          @Override
          public void run() {
            final ListTableModel<ItemInfo> bookModel = new ListTableModel<>(new ColumnInfo[]{title, date}, itemInfoList);
            bookTable.setModelAndUpdateColumns(bookModel);
          }
        });

      }
    };

    ProgressManager.getInstance().run(backBookTask);


    FutureTask<List<ItemInfo>> readhub = new FutureTask<>(() -> {

      List<ItemInfo> itemInfoList = new ArrayList<>();

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
      return itemInfoList;

    });

    new Thread(readhub).start();
    tasks.add(readhub);


    Task.Backgroundable backNewTask = new Task.Backgroundable(null, "Loading news", true) {
      @Override
      public void run(@NotNull final ProgressIndicator indicator) {
        List<ItemInfo> allItems = new ArrayList<>();
        for (FutureTask<List<ItemInfo>> task : tasks) {
          try {
            List<ItemInfo> itemInfos = task.get();
            allItems.addAll(itemInfos);
          }
          catch (InterruptedException e) {
            e.printStackTrace();
          }
          catch (ExecutionException e) {
            e.printStackTrace();
          }
        }


        ApplicationManager.getApplication().invokeLater(new Runnable() {
          @Override
          public void run() {
            final ListTableModel<ItemInfo> newModel = new ListTableModel<>(new ColumnInfo[]{title, date}, allItems);
            newsTable.setModelAndUpdateColumns(newModel);
          }
        });

      }
    };

    ProgressManager.getInstance().run(backNewTask);


    Task.Backgroundable blogkTask = new Task.Backgroundable(null, "Loading blog", true) {
      @Override
      public void run(@NotNull final ProgressIndicator indicator) {
        List<ItemInfo> itemInfoList = new ArrayList<>();

        try {


          {

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

          }

          {

            String from = "yinwang >";

            List<String> links2 = Arrays.asList("http://www.yinwang.org");
            for (String link : links2) {
              Document doc = Jsoup.connect(link).get();

              Elements select = doc.select("li.list-group-item");

              for (Element item : select) {

                Elements items = item.select("a");
                Elements dates = item.select("div.date");

                ItemInfo itemInfo = new ItemInfo();
                itemInfo.setTitle(from + items.first().text());
                itemInfo.setDate(dates.first().text());
                itemInfoList.add(itemInfo);
              }
            }

          }

          {

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

          }

          {

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

          }


          {

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

          }


        }
        catch (Exception e) {
          e.printStackTrace();
        }


        ApplicationManager.getApplication().invokeLater(new Runnable() {
          @Override
          public void run() {
            final ListTableModel<ItemInfo> bookModel = new ListTableModel<>(new ColumnInfo[]{title, date}, itemInfoList);
            blogTable.setModelAndUpdateColumns(bookModel);
          }
        });

      }
    };

    ProgressManager.getInstance().run(blogkTask);


    JScrollPane newTableScroll = ScrollPaneFactory.createScrollPane(newsTable, SideBorder.TOP);
    JScrollPane bookTableScroll = ScrollPaneFactory.createScrollPane(bookTable, SideBorder.TOP);
    JScrollPane blogTableScroll = ScrollPaneFactory.createScrollPane(blogTable, SideBorder.TOP);


    toolWindow.getContentManager().addContent(ContentFactory.SERVICE.getInstance().createContent(newTableScroll, "资讯", false));
    toolWindow.getContentManager().addContent(ContentFactory.SERVICE.getInstance().createContent(bookTableScroll, "书讯", false));
    toolWindow.getContentManager().addContent(ContentFactory.SERVICE.getInstance().createContent(blogTableScroll, "博客", false));

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
