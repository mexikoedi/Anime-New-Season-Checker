# Anime New Season Checker

## What is this?
This is a Java program with a GUI which lets you check if an anime got a new season, how many seasons the anime already has and their runtime.

## Is this useful to me?
If you already use websites/applications/... which notify you every time an anime gets a new season then no. <br>
But otherwise if you always miss new seasons or if you are a person who uses bookmarks and checks them after sometime via manually searching in the browser for example then this might be something for you.

## How to install?
**You need Java! Here: https://www.java.com/en/download** <br>
You can download it if you select the green button named "Code" then "Download ZIP" or download the ZIP file over the releases tab. <br>
Extract the downloaded zip folder and then open the program by opening the .jar file. <br>
Before opening the .jar file open the "list.txt" file and add your anime titles and URLs to it. <br>
**Read below for more important information.**

## Limitations?
1) "list.txt" cannot be empty. <br>
2) Don't use ":" after "anime:". <br>
3) Only use website URLs from [IMDB](https://www.imdb.com). <br>
4) The .jar file and the "list.txt" file need to be in the same directory. <br>
5) **Follow the examples below.**

## How does it work?
After you entered a season number you need to click confirm and afterwards the search button. <br>
The Java program connects to the given IMDB URLs and gets the data from there (seasons/runtime). <br> 
After that it checks if the data is correct and formats it accordingly. <br>
At the end all the data fills the text fields in the GUI.

## How fast is this program working?
It will get slower and slower the more anime titles you add because it needs to connect to each IMDB URL and check every single one of them until it can fill the text fields.

For example: <br>
To get the data for 150 anime titles the program needs roughly 3.6 minutes.

## Is the program working correctly?
Most of the time yes, but there are rare cases where for example a season is split in half and registered as new season or because of OVAs which are wrongly registered as new season or because of weird runtime information which were made at the IMDB website. <br>
This is a fault of IMDB and not of the program. <br>
If you see wrong data you could try to fix the data at the IMDB website. <br>
I tested it with over 150 anime titles and encountered 3 wrong season information (1 season too much) and 2 weird runtime information.

## Examples?
![list.txt](https://i.ibb.co/mDnLMdd/example1.png "list.txt") <br>
![GUI](https://i.ibb.co/R3qWfzN/example2.png "GUI") <br>
![Program use](https://i.ibb.co/12BkZJh/example3.png "Program use")

## Third-party information
```
jsoup License
The jsoup code-base (including source and compiled packages) are distributed under the open source MIT license as described below.

The MIT License
Copyright ?? 2009 - 2022 Jonathan Hedley (https://jsoup.org/)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```

?? 2022 mexikoedi 

All rights reserved.
