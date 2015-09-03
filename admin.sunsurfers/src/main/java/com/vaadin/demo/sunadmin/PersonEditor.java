package com.vaadin.demo.sunadmin;

import com.vaadin.data.Item;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.demo.sunadmin.domain.Sunsurfer;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

@SuppressWarnings("serial")
public class PersonEditor extends Window implements Button.ClickListener,
        FormFieldFactory {

    private final Item personItem;
    private Form editorForm;
    private Button saveButton;
    private Button cancelButton;

    public PersonEditor(Item personItem) {
        this.personItem = personItem;
        editorForm = new Form();
        editorForm.setFormFieldFactory(this);
        editorForm.setBuffered(true);
        editorForm.setImmediate(true);
        editorForm.setItemDataSource(personItem, Arrays.asList("nickname", "email",
                "password", "name", "surname", "living", "public_status",
                "description", "status", "instagram_id", "facebook_id", "vkontakte_id"));

        saveButton = new Button("Save", this);
        cancelButton = new Button("Cancel", this);

        editorForm.getFooter().addComponent(saveButton);
        editorForm.getFooter().addComponent(cancelButton);
        setSizeUndefined();
        setContent(editorForm);
        setCaption("Editor");
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton() == saveButton) {
            editorForm.commit();
            fireEvent(new EditorSavedEvent(this, personItem));
        } else if (event.getButton() == cancelButton) {
            editorForm.discard();
        }
        close();
    }


    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {
        Field field = DefaultFieldFactory.get().createField(item, propertyId,
                uiContext);
        if (field instanceof TextField) {
            ((TextField) field).setNullRepresentation("");
        }

        field.addValidator(new BeanValidator(Sunsurfer.class, propertyId
                .toString()));

        return field;
    }

    public void addListener(EditorSavedListener listener) {
        try {
            Method method = EditorSavedListener.class.getDeclaredMethod(
                    "editorSaved", new Class[] { EditorSavedEvent.class });
            addListener(EditorSavedEvent.class, listener, method);
        } catch (final java.lang.NoSuchMethodException e) {
            throw new java.lang.RuntimeException(
                    "Internal error, editor saved method not found");
        }
    }

    public void removeListener(EditorSavedListener listener) {
        removeListener(EditorSavedEvent.class, listener);
    }

    public static class EditorSavedEvent extends Component.Event {

        private Item savedItem;

        public EditorSavedEvent(Component source, Item savedItem) {
            super(source);
            this.savedItem = savedItem;
        }

        public Item getSavedItem() {
            return savedItem;
        }
    }

    public interface EditorSavedListener extends Serializable {
        public void editorSaved(EditorSavedEvent event);
    }

}
