
// Compile with: g++ -o out prog.cc -lX11
// Run with: ./out

// A good on-line manual: http://tronche.com/gui/x/xlib/

/* include the X library headers */
#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/Xos.h>

/* include C/C++ headers */
#include <stdio.h>
#include <stdlib.h>
#include <iostream>

#define MAX 100

/* Global variables */
Display *dis; //Pointer to the X server
int screen;   //Our Screen
Window win;   //Our Main Window
GC gc;        //The Graphics Context


int xArray[MAX];
int yArray[MAX];
int clicks = 0;

void initX() {        
	unsigned long black,white;

	dis=XOpenDisplay(NULL); //Get a connection to the default X Server.
   	screen=DefaultScreen(dis); //Get the default screen. Could be 1 of many.
	black=BlackPixel(dis,screen);
	white=WhitePixel(dis, screen);
   	win=XCreateSimpleWindow(dis,DefaultRootWindow(dis),0,0,
		640, 480, 5, white, black);
        /*
		Create a Window with parent DefaultRootWindow(dis)
		at 0,0 in the parent (the default screen) of size 640 by 480
		The Border width is 5 pixels and white.
		The background colour of the window is black
        */
	XSetStandardProperties(dis,win,"My first X Window","Hi",None,NULL,0,NULL);
	/*
		Window Title, the Icon's name, No bitmap for icon, argv, argc, hints
	*/
	XSelectInput(dis, win, ExposureMask|ButtonPressMask|KeyPressMask|EnterWindowMask|LeaveWindowMask);
	/*
		What events are we interested in!  We will not get any others.
	*/
        gc=XCreateGC(dis, win, 0,0);  	//Create a Graphics Context   
	XSetBackground(dis,gc,black);	
	XSetForeground(dis,gc,white);
	XClearWindow(dis, win);
	XMapRaised(dis, win); //Display the window and generate an Expose event. makes window top window
	//Don't forget the above ... ever!
};

void closeX() {
	XFreeGC(dis, gc);
	XDestroyWindow(dis,win);
	XCloseDisplay(dis);
        std::cout << "Program terminated!" << std::endl;	
	exit(1);				
};

void drawClicks(){
    char text[255];
    for(int i=0;i<clicks;i++){
        std::cout << "HELLO\n";
        sprintf(text,"You pressed the mouse at: (%d,%d)",xArray[i], yArray[i]);
		XDrawString(dis,win,gc,xArray[i],yArray[i], text, strlen(text));
    }
};

void redraw() {
        std::cout << "Redraw the Window" << std::endl;
	//XClearWindow(dis, win);
    drawClicks();
};

int main () {
	XEvent event;		// the XEvent
	KeySym key;		// used to handle KeyPress Events	
	char text[255];		// buffer for KeyPress Events and output
        unsigned int k;         // used for processing key events
        int done = 0;           // used to control main loop
        int count;		// used for key conversions
	int x,y;		// used for mouse coordinates

	initX();

	// Event loop
        // Note:  only events we set the mask for are recevied!
	while(!done) {		
		XNextEvent(dis, &event); // Get the next event. blocking
                std::cout << "Event "<< event.type << std::endl;

	        switch(event.type){

		   case KeyPress: //Event 2  
		        count = XLookupString(&event.xkey,text,255,&key,0);
		        if(count==1)
                            std::cout << "you pressed the " << text[0] << " key!" << std::endl;
                        
                        k = XLookupKeysym(&event.xkey, 0); //Look up keysymdef.h for all the definitions
                        if (k == XK_F1){
                            std::cout << "You pressed the F1 key" << std::endl;
                        }	
                         if (k == XK_Shift_L){
                            std::cout << "You pressed the left shift key" << std::endl;
                        }	
                        if (k == XK_Escape || text[0]=='q') {
		          done = 1;
		        }
                        break;
	
		   case ButtonPress://Event 4
			
            x=event.xbutton.x;
			y=event.xbutton.y;
            
            xArray[clicks] = x;
            yArray[clicks++] = y;
            clicks = clicks%MAX;
            
            sprintf(text,"You pressed the mouse at: (%d,%d)",x, y);
			XDrawString(dis,win,gc,x,y, text, strlen(text));
                        break;

            
           case LeaveNotify:
            std::cout << "The Mouse has left the Window!" << std::endl;
            break;
            
		   case EnterNotify: //Event 7
			std::cout << "The Mouse has entered the Window!" << std::endl;
			break;

                   case Expose://Event 12
		        if (event.xexpose.count==0) {
                   // the window was exposed redraw it!
			   redraw();
                        }
		        break;

		}
	}//endwhile

        closeX(); //Shut X down nicely!
}



