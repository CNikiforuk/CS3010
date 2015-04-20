CC = g++
SRC = prog.cc
ARG = -lX11


make: clean all
all:
	$(CC) -o prog $(SRC) $(ARG)
clean: 
	rm -f $(OBJ) $(TAR) prog
tar:
	$(TC) $(TAR) ./
