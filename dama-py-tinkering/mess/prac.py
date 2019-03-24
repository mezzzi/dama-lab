from tkinter import *
win = Tk()
menu = Menu(win, tearoff = 0)
def callb():
	print('yeah')

	
menu.add_command(label='comA', command = callb)
menu.add_command(label='comB', command = callb)
def popup(event):
	menu.post(event.x_root, event.y_root)

	
canvas = Canvas(win, width = 200, height=100)
canvas.pack()
canvas.bind("<Button-3>", popup)
