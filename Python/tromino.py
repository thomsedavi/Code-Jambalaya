from Tkinter import *

root = Tk()

trominoes = []
screenSize = 512

class Tile:
    def __init__(self, x_centre, y_centre, orientation):
        self.x_centre = x_centre
        self.y_centre = y_centre
        self.orientation = orientation
        

class App:
    def __init__(self, master):
        frame = Frame(master)
        frame.pack()

        self.label_size = Label(frame, text="n, a power of 2 (max 256)")
        self.label_size.pack(side=TOP)

        self.size_form = Entry(frame)
        self.size_form.pack(side=TOP)

        self.label_x = Label(frame, text="x coordinate of missing square")
        self.label_x.pack(side=TOP)

        self.x_form = Entry(frame)
        self.x_form.pack(side=TOP)

        self.label_y = Label(frame, text="y coordinate of missing square")
        self.label_y.pack(side=TOP)

        self.y_form = Entry(frame)
        self.y_form.pack(side=TOP)

        self.console = Button(frame, text="Print to Console", command=self.print_to_console)
        self.console.pack(side=TOP)

        self.screen = Button(frame, text="Print to Screen", command=self.print_to_screen)
        self.screen.pack(side=TOP)

        self.drawing_area = Canvas(frame, width = screenSize, height = screenSize)
        self.drawing_area.pack(side=TOP)

    def print_to_console(self):
        App.tile_init(self)
        print "Size = " + self.size_form.get() + "*" + self.size_form.get() + ", hole at (" + self.x_form.get() + "," + self.y_form.get() + ")"
        for tromino in trominoes:
            result = str(tromino.x_centre) + " " + str(tromino.y_centre) + " " + tromino.orientation
            print result


    def print_to_screen(self):
        App.tile_init(self)
        scale = screenSize/int(self.size_form.get())
        line_dist = scale*0.5
        self.drawing_area.delete(ALL)
        for tromino in trominoes:
            # All of the y-coordinate stuff is backwards so everything gets translated from the bottom left corner
            x_cord = scale*tromino.x_centre
            y_cord = scale*(int(self.size_form.get())-tromino.y_centre)
            if tromino.orientation == "UR":
                self.drawing_area.create_line(x_cord-line_dist,y_cord-line_dist,x_cord+line_dist,y_cord-line_dist, fill="blue")
                self.drawing_area.create_line(x_cord+line_dist,y_cord-line_dist,x_cord+line_dist,y_cord+line_dist, fill="blue")
            elif tromino.orientation == "LR":
                self.drawing_area.create_line(x_cord-line_dist,y_cord+line_dist,x_cord+line_dist,y_cord+line_dist, fill="blue")
                self.drawing_area.create_line(x_cord+line_dist,y_cord+line_dist,x_cord+line_dist,y_cord-line_dist, fill="blue")
            elif tromino.orientation == "UL":
                self.drawing_area.create_line(x_cord-line_dist,y_cord+line_dist,x_cord-line_dist,y_cord-line_dist, fill="blue")
                self.drawing_area.create_line(x_cord-line_dist,y_cord-line_dist,x_cord+line_dist,y_cord-line_dist, fill="blue")
            else:
                self.drawing_area.create_line(x_cord-line_dist,y_cord+line_dist,x_cord-line_dist,y_cord-line_dist, fill="blue")
                self.drawing_area.create_line(x_cord-line_dist,y_cord+line_dist,x_cord+line_dist,y_cord+line_dist, fill="blue")

    def tile_init(self):
        global trominoes
        if 1 == 1:
            trominoes = []

            size = int(self.size_form.get())
            x_hole = int(self.x_form.get())
            y_hole = int(self.y_form.get())

            if (x_hole >= size) | (y_hole >= size):
                raise

            check = size

            # plz only be a square of 2!
            while check != 2:
                if check%2 == 1:
                    raise
                else:
                    check /= 2

            self.tile(0, 0, size, x_hole, y_hole)

#        except:
#            print("error 44757f3 please input better!")

    def tile(self, x_offset, y_offset, size, x_hole, y_hole):
        global trominoes
        if size == 2:
            x_centre = x_offset + 1
            y_centre = y_offset + 1
            if x_hole - x_offset == 0:
                if y_hole - y_offset == 0:
                    trominoes.append(Tile(x_centre, y_centre, "UR"))
                else:
                    trominoes.append(Tile(x_centre, y_centre, "LR"))
            else:
                if y_hole - y_offset == 0:
                    trominoes.append(Tile(x_centre, y_centre, "UL"))
                else:
                    trominoes.append(Tile(x_centre, y_centre, "LL"))
        else:
            half_size = size/2
            x_centre = x_offset + half_size
            y_centre = y_offset + half_size
            if x_hole < x_centre:
                if y_hole < y_centre:
                    trominoes.append(Tile(x_centre, y_centre, "UR"))
                    self.tile(x_offset, y_offset, half_size, x_hole, y_hole)
                    self.tile(x_offset + half_size, y_offset, half_size, x_offset + half_size, y_offset + half_size - 1)
                    self.tile(x_offset + half_size, y_offset + half_size, half_size, x_offset + half_size, y_offset + half_size)
                    self.tile(x_offset, y_offset + half_size, half_size, x_offset + half_size - 1, y_offset + half_size)
                else:
                    trominoes.append(Tile(x_centre, y_centre, "LR"))
                    self.tile(x_offset, y_offset, half_size, x_offset + half_size - 1, y_offset + half_size - 1)
                    self.tile(x_offset + half_size, y_offset, half_size, x_offset + half_size, y_offset + half_size - 1)
                    self.tile(x_offset + half_size, y_offset + half_size, half_size, x_offset + half_size, y_offset + half_size)
                    self.tile(x_offset, y_offset + half_size, half_size, x_hole, y_hole)
            else:
                if y_hole < y_centre:
                    trominoes.append(Tile(x_centre, y_centre, "UL"))
                    self.tile(x_offset, y_offset, half_size, x_offset + half_size - 1, y_offset + half_size - 1)
                    self.tile(x_offset + half_size, y_offset, half_size, x_hole, y_hole)
                    self.tile(x_offset + half_size, y_offset + half_size, half_size, x_offset + half_size, y_offset + half_size)
                    self.tile(x_offset, y_offset + half_size, half_size, x_offset + half_size - 1, y_offset + half_size)
                else:
                    trominoes.append(Tile(x_centre, y_centre, "LL"))
                    self.tile(x_offset, y_offset, half_size, x_offset + half_size - 1, y_offset + half_size - 1)
                    self.tile(x_offset + half_size, y_offset, half_size, x_offset + half_size, y_offset + half_size - 1)
                    self.tile(x_offset + half_size, y_offset + half_size, half_size, x_hole, y_hole)
                    self.tile(x_offset, y_offset + half_size, half_size, x_offset + half_size - 1, y_offset + half_size)

app = App(root)

root.mainloop()