/* 
A sample program that uses gtk+3.0

Compile with: gcc -o out gtkExample.c `pkg-config --cflags --libs gtk+-3.0`
              g++ works too.

	pkg-config is a helper tool used when compiling applications and libraries. It helps 
	you insert the correct compiler options on the command line

	A GTK+ tutorial is at: http://zetcode.com/tutorials/gtktutorial/
        GTK+ documentation is at: https://developer.gnome.org/
*/
#include <cairo.h>
#include <gtk/gtk.h>

GtkWidget *window;
GtkWidget *frame;
GtkWidget *button;
GtkWidget *label;
GtkWidget *text;
GtkWidget *darea;


static void draw(cairo_t *); //forward declaration

//Call-Back: called when repaint is needed
static gboolean onDrawEvent(GtkWidget *widget, cairo_t *cr, gpointer user_data){      
  draw(cr);
  return FALSE;
}

//Custom drawing code
static void draw(cairo_t *cr){
  cairo_set_source_rgb(cr, 0.7, 0, 0);// 0..1
  cairo_select_font_face(cr, "Sans", CAIRO_FONT_SLANT_NORMAL,
      CAIRO_FONT_WEIGHT_NORMAL);
  cairo_set_font_size(cr, 30.0);

  cairo_move_to(cr, 10.0, 70.0);
  cairo_show_text(cr, "Drawing with Cairo");

  cairo_set_source_rgb(cr, 0.3, 0.3, 1);
  cairo_rectangle(cr, 150, 150, 50, 20);
  cairo_fill(cr);    
}


//Put up a "Are you sure you want to quit?" dialog.  This is Modal.
gboolean doYouWantToQuit(){
  GtkWidget *dialog;
  gint response;
  dialog = gtk_message_dialog_new(GTK_WINDOW(window),
            GTK_DIALOG_DESTROY_WITH_PARENT,
            GTK_MESSAGE_QUESTION,
            GTK_BUTTONS_YES_NO,
            "Are you sure you want to quit?");
  gtk_window_set_title(GTK_WINDOW(dialog), "Please don't leave me!");
  response = gtk_dialog_run(GTK_DIALOG(dialog));//the dialog is managed
  gtk_widget_destroy(dialog);
  if(response == GTK_RESPONSE_YES) {
    return FALSE; //I know it seems wrong!
  }
  else return TRUE;
}

//A Call-Back: Called when the button is pressed
void pressed (GtkWidget *widget, gpointer   data){
  g_print ("Thanks for pressing me!\n");
  const gchar *str = gtk_entry_get_text(GTK_ENTRY(text));
  gtk_label_set_text(GTK_LABEL(label),str); //gtk_label_set_text((GtkLabel*)label,str);
}

//A Call-Back: Called when the "Window Close" button is pressed.
gboolean terminate (GtkWidget *widget, GdkEvent  *event, gpointer data){
  // Return TRUE to not terminate
  // Return FALSE to terminate

  return doYouWantToQuit();
}

//A Call-Back: Called when mouse enters the button
void enterButton(GtkWidget *widget, gpointer data) { 
  g_print("In Button\n");
}

int main (int   argc, char *argv[]){
  gtk_init (&argc, &argv);

  window = gtk_window_new (GTK_WINDOW_TOPLEVEL);
  gtk_window_set_default_size(GTK_WINDOW(window), 300, 200);
  gtk_window_set_title (GTK_WINDOW (window), "Hello");

  frame = gtk_fixed_new(); //A container that requires "hard" positions of widgets

  //Register the call-backs
  //Handle the "Window Close" button "delete-event" event
  g_signal_connect (window, "delete-event", G_CALLBACK (terminate), NULL);//data is NULL

  //Handle the "destroy" event to the gtk_main_quit() function.
  /*
     This signal is emitted when we call gtk_widget_destroy() on the window,
     or if we return FALSE in the "delete_event" callback.
     If we don't do this the program does not terminate even though the window closes.
  */
  g_signal_connect (window, "destroy", G_CALLBACK (gtk_main_quit), NULL);

  gtk_container_set_border_width (GTK_CONTAINER (window), 4);//Border width of 10 pixels

  //Make a custom drawing area and add it to the frame container
  darea = gtk_drawing_area_new();
  gtk_fixed_put(GTK_FIXED(frame), darea, 0,0);
  gtk_widget_set_size_request (darea, 300, 200);//Same size as initial window

  //repaint the drawing area when requested by Window System "draw" event
  g_signal_connect(G_OBJECT(darea), "draw",G_CALLBACK(onDrawEvent), NULL); 

  //Make a Label and position it
  label = gtk_label_new("Hi I'm a label");
  gtk_fixed_put(GTK_FIXED(frame), label, 100, 10);

  //Make a text entry field
  text = gtk_entry_new();
  gtk_fixed_put(GTK_FIXED(frame), text, 100, 100);

  //Make a button and set up a "clicked" handler
  button = gtk_button_new_with_label ("Press Me!");
  gtk_fixed_put(GTK_FIXED(frame), button, 100, 200);
  g_signal_connect (button, "clicked", G_CALLBACK (pressed), NULL);
  g_signal_connect(button, "enter", G_CALLBACK(enterButton), NULL);

  //Add the button to a container and add the container to the window
  gtk_container_add (GTK_CONTAINER (window), frame);

  //Make everything visible
  gtk_widget_show_all(window);

  gtk_main ();//Main loop which handle events
  g_print("We are done!\n");

  return 0;
}
