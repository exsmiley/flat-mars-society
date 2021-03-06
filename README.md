# Flat-Mars-Society

Clone this repo. Then follow the normal battlecode running instructions.

## How to Run

Run `sudo sh run.sh` in the directory.

After some downloady thingies...

`To play games open http://localhost:6147/run.html in your browser`

## How to Setup git

Run this:

`git remote add bc https://github.com/battlecode/bc18-scaffold.git `


## How to Update Battlecode
The above command will allow us to pull new versions from the battlecode repo via: 
`git pull bc master --allow-unrelated-histories`

# Original Battlecode Stuff

Follow its instructions, and start runnin them games! (warning, socket.bc18map may be buggy rn)

### Windows 10 Pro

Windows 10 Professional or Enterprise: install https://www.docker.com/docker-windows

double click run.cmd

wait a while

When you get the message: `To play games open http://localhost:6147/run.html in your browser` you're good to go!

### Windows 7, 8, 10 Home

First, install Docker Toolbox: https://docs.docker.com/toolbox/toolbox_install_windows/

Download the executable and follow the wizard to install it. 

Search for 'Docker Quickstart Terminal' and open it. Do not open it twice, because multiple running copies do not work properly.

Wait a few minutes. You will see a picture of a whale when it finishes. Later, you will enter commands in this docker quickstart terminal, so don't close it.

Download the battlecode 2018 scaffold from https://github.com/battlecode/bc18-scaffold. You can use the green button to download the files and then manually unzip them, or you can clone the repository using Github if you're familiar with that. 

Check that you have at least 2 GB of space available on your hard drive. You will need this much space for the docker install. 

In the quickstart terminal, navigate to the location of the unzipped files that you downloaded. You can navigate using 'cd' and 'ls'. For more information on navigating a file system with a unix terminal, see https://www.digitalocean.com/community/tutorials/basic-linux-navigation-and-file-management. 

Once you have navigated to where your bc18-scaffold repository is located, type "bash run.sh" (then press enter) to start the battlecode server. The first time you run this command, it will take a while downloading the docker installation. 

The Battlecode server will initialize and you will see the prompt, `To play games open http://localhost:6147/run.html in your browser on Mac/Linux/WindowsPro, or http://192.168.99.100:6147/run.html on Windows10Home. `. Do not close the docker quickstart window. Go to the second url http://192.168.99.100:6147/run.html and you should see a website. You run Battlecode matches using this website graphical user interface (GUI). The docker quickstart window will not accept commands at this point; it is listening to the GUI. 

Run a test match using the GUI. Select "Run Game" at the lower right. Live player logs should appear on screen. When the match ends, the live logs will disappear and a message appears on the website indicating which player won. Also, a match file is produced. The default name of the match file is "replay.bc18". This file appears in the bc18-scaffold directory that has the run.sh file. 

To see a visual representation of the match, you can load the match file using the viewer. First, let's download the viewer.

Visit http://battlecode.org/#/materials/releases and select the "Windows" link under the bullet point, "Download the Viewer". 

Unzip the file you downloaded.

Before running the viewer, reduce your system volume to a reasonable level. The viewer plays music. 

Double-click clientWindows.exe. Select your choice of resolution. Windowed mode is convenient for running next to your code. Once you have configured the player, click "Play!"

Now load the match file by selecting the icon with three bars at the top of the screen. Navigate to the bc18-scaffold directory, where the replay.bc18 file is stored. Then click the file, and at the bottom of the window click "Select". Now the replay file is loaded, so start the replay running using the triangle button in the top left. You can zoom out with the mouse scroll wheel, and pan left/right/up/down with the arrow keys. 

### Writing your own bot

To get started with your bot you can modify one of the examplefuncs-players located in the bc18-scaffold directory. You can modify the run.py for python, the main.c for c, and player.java file for java. Then use the web interface to queue a game as before.

Players are named after the directory where they are located. For example, the player "examplesfuncplayer-python" corresponds to the folder where the python code is located. 

You can create a new player by copying the examplefuncsplayer and then renaming the folder. The website interface can't see your new folder until you refresh the website. Select the website and press F5 or ctrl+r. Your new bot will then appear in the dropdown menu. 

#### FAQ (for Docker Toolbox):
1. How do I play a game?

 Ignore the variables for the time being unless you want to specifically test something intensive. Select players from the dropdowns on the right as well as a map, and click "Run Game". A pop-up will display showing the logs of each of the 4 bots (2 bots x 2 maps) while the game runs. When the game ends, the pop-up will disappear and the replay will be in the scaffold folder (the same folder where the bots are located).

2. How can I see the replay?

 Download the viewer client from http://battlecode.org/#/materials/releases for your operating system. Click on the button with 3 horizontal bars, which will bring up a filesystem that you can select the replay from. Navigate to it, click on the replay, and click "select" in the bottom right of the filesystem window. Then, click the play button in the top right. You can zoom out using the mouse wheel and move your vision using WASD or the arrow keys.

3. I ran a game with a broken bot, and now I can't run another game!

 Open another Docker Quickstart window (start.sh) and type the following command: `docker ps`. This will display the information of the currently running container. Take note of the container ID (the first entry) and type `docker kill {ID}`. You only need to type the first couple characters, e.g. `docker kill e06`. Go back to the original quickstart window and type `bash run.sh` again.

4. Running `bash run.sh` keeps downloading a lot of garbage files!

 Again open another Docker Quickstart window (start.sh) and type the following command: `docker container ls -a`. This will display the information of all containers, including the running one. Type `docker container rm {id}` for each of the non-running containers. Again you only need to type the first few characters, e.g. `docker container rm ff4`.

 A possibly more robust solution to this problem is via the `--rm` flag. Running `bash run.sh --rm` should, in theory, remove the container after termination.

5. The Python example bot explodes!

 There is currently an API bug tied to building blueprints. If you avoid `gc.blueprint()` then you should be fine for the time being.
 
6. Docker is taking up too much memory! / I get out of memory errors when running `run.sh`!

 Docker saves some information between runs called images, containers, and volumes. Unchecked, these can quickly take over many gigs of storage. Occasionally, run the following 4 lines:

 `docker stop $(docker ps -q)`
`docker container rm $(docker container ls -aq)`
`docker volume rm $(docker volume ls -q)$`
`docker volume prune`

 which should free up a lot of space.

7. I get a 'We dun goof no map found' error!

 Ignore it. This should happen when using the test map.

8. I don't see any players/maps in the dropdown and/or my console says that `/player` can't be found!

 This one is pretty tricky to track down. Try looking at your `run.sh` file and changing `/players` to `/player` if necessary. You can also try running the `pwd` command in your scaffold directory and replacing `$PWD` in `run.sh` with that absolute path. Additionally make sure your filepath doesn't contain any spaces, special characters, etc. that may confuse the parser.
 
9. The Python bot crashes with an error of the form `can't open file run.py`!

 The issue is most likely that you are using Windows line endings (\r\n) rather than Unix line endings (\n) which confuses the shell script and gives some silly error. To fix this, make sure to convert all your `run.sh` files to Unix format before running a game. You can accomplish this with an editor like Notepad++ (Edit -> EOL Conversion -> Unix/OSX Format) or the `dos2unix` command line tool (e.g. `dos2unix */run.sh`).
>>>>>>> 41084a3c4eedf365a164a74b585d6236f5743389
