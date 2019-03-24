from tkinter import * # Import all definitions from tkinter

class AnimationDemo:
    def __init__(self):
        window = Tk() # Create a window
        window.title("Animation Demo") # Set a title
        width = 250 # Width of the canvas
        canvas = Canvas(window, bg="white", width = 500,
                        height = 500)
        for i in range(10):
            for j in range(10):
                canvas.create_rectangle(j * 50, i * 50, (j+1)*50,
                                        (i+1)*50, fill="green")
        canvas.pack()
        self.photo = PhotoImage(file="kingcoke.png")
        x = 0 # Starting x position
        idnum = canvas.create_image(50, 50, image=self.photo, tag = "text")
        canvas.tag_bind(idnum,
                            "<ButtonPress-1>", self.cellClicked)
        
        dx = 5
        count = 0
        while True:
            count += 1
            if count == 10:
                self.photo = PhotoImage(file="coke.png")
                canvas.itemconfig(idnum, image = self.photo)
            canvas.move(idnum, dx, dx)
            canvas.after(100)
            canvas.update()
            if x < width:
                x += dx
        
        window.mainloop()
    def cellClicked(self, event):
        print("Clicked")
AnimationDemo()
