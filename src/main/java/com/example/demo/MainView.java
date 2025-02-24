package com.example.demo;


import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends VerticalLayout {
    private PersonRepository repository;
    private TextField firstname = new TextField("First Name");
    private TextField lastname = new TextField("Last Name");
    private TextField height = new TextField("Height (in cm)");
    private TextField weight = new TextField("Weight (in kg)");
    private TextField sex = new TextField("Sex at birth");

    private Binder<PersonInfo> binder = new Binder<>(PersonInfo.class);

    public MainView(PersonRepository repository){
        this.repository = repository;
        add(new H1("Lets get to know you: "));



    }

}
