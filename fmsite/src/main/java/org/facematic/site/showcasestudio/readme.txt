Здесь показана конструкция страницы "demo" нашего сайта.

Страница "demo" состоит из:

- дерева с перечнем разделов демонстрации 
<Tree name="mainSelector" nullSelectionAllowed="false" sizeFull="true"  expandRatio="100" itemCaptionPropertyId="value" />

- пространства отображения содержимого каждого примера (ShowCaseViewer)
<Overlay name="overlay" sizeFull="true" expandRatio="100">

Элементы оверлея добаввляются в методе ShowCaseStudio.init:

  init () {
  ... 
        ShowCaseViewer viewerController = new ShowCaseViewer(c);
		FaceProducer producer = new FaceProducer(viewerController, ui);
		Component view = producer.getViewFor(ShowCaseViewer.class);
		view.setCaption(description);
		overlay.addElement(view);  }
   ...	
</Overlay>
  
