/**
 * Copyright 2009-2013 Oy Vaadin Ltd
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.demo.sunadmin;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;
import com.vaadin.demo.sunadmin.PersonEditor.EditorSavedEvent;
import com.vaadin.demo.sunadmin.PersonEditor.EditorSavedListener;
import com.vaadin.demo.sunadmin.domain.Sunsurfer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

public class MainView extends VerticalLayout implements
        ComponentContainer {

    private Table personTable;

    private TextField searchField;

    private Button newButton;
    private Button deleteButton;
    private Button editButton;

    private JPAContainer<Sunsurfer> sunsurfers;

    private String textFilter;

    public MainView() {
        sunsurfers = JPAContainerFactory.make(Sunsurfer.class,
                SunUI.PERSISTENCE_UNIT);
        buildMainArea();
    }


    private void buildMainArea() {
        personTable = new Table(null, sunsurfers);
        personTable.setSelectable(true);
        personTable.setImmediate(true);
        personTable.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                setModificationsEnabled(event.getProperty().getValue() != null);
            }

            private void setModificationsEnabled(boolean b) {
                deleteButton.setEnabled(b);
                editButton.setEnabled(b);
            }
        });

        personTable.setSizeFull();
        personTable.setHeight("100%");
        personTable.setPageLength(30);
        personTable.setSelectable(true);
        personTable.addListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    personTable.select(event.getItemId());
                }
            }
        });

        personTable.setVisibleColumns(new Object[]{"nickname", "email",
                "password", "name", "surname", "living", "public_status",
                "description", "status", "instagram_id", "facebook_id", "vkontakte_id"});

        HorizontalLayout toolbar = new HorizontalLayout();
        newButton = new Button("Add");
        newButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                final BeanItem<Sunsurfer> newSunsurferItem = new BeanItem<Sunsurfer>(
                        new Sunsurfer());
                PersonEditor personEditor = new PersonEditor(newSunsurferItem);
                personEditor.addListener(new EditorSavedListener() {
                    @Override
                    public void editorSaved(EditorSavedEvent event) {
                        sunsurfers.addEntity(newSunsurferItem.getBean());
                    }
                });
                UI.getCurrent().addWindow(personEditor);
            }
        });

        deleteButton = new Button("Delete");
        deleteButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                sunsurfers.removeItem(personTable.getValue());
            }
        });
        deleteButton.setEnabled(false);

        editButton = new Button("Edit");
        editButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                UI.getCurrent().addWindow(
                        new PersonEditor(personTable.getItem(personTable
                                .getValue())));
            }
        });
        editButton.setEnabled(false);

        searchField = new TextField();
        searchField.setInputPrompt("Search by name");
        searchField.addTextChangeListener(new TextChangeListener() {

            @Override
            public void textChange(TextChangeEvent event) {
                textFilter = event.getText();
                updateFilters();
            }
        });

        toolbar.addComponent(newButton);
        toolbar.addComponent(deleteButton);
        toolbar.addComponent(editButton);
        toolbar.addComponent(searchField);
        toolbar.setWidth("100%");
        toolbar.setExpandRatio(searchField, 1);
        toolbar.setComponentAlignment(searchField, Alignment.TOP_RIGHT);
        addComponent(toolbar);
        addComponent(personTable);
    }

    private void updateFilters() {
        sunsurfers.setApplyFiltersImmediately(false);
        sunsurfers.removeAllContainerFilters();

        if (textFilter != null && !textFilter.equals("")) {
            Or or = new Or(new Like("nickname", textFilter + "%", false),
                    new Like("email", textFilter + "%", false));
            sunsurfers.addContainerFilter(or);
        }
        sunsurfers.applyFilters();
    }
}
