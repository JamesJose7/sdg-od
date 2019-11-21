# About

The development of this application was part of a bachelor thesis. Its purpose is to implement the proposals stated in the research process and show the expected results. For more info about this project, you can find the full document in this [link](http://dspace.utpl.edu.ec/handle/20.500.11962/24629).

## Authors

- [José Eguiguren](https://github.com/JamesJose7)
- [Nelson Piedra](https://investigacion.utpl.edu.ec/es/nopiedra)

## Live Demo

http://eva4all.utpl.edu.ec/sdg-od/

This deployment is missing a hosted triplestore to serve as it's semantic backend. Thus, many of its features do not work as intended yet.

# User documentation

The following sections describe the main features of the web application, how to use them, and what to expect from them.

## Main Page
This page briefly describes the app's objective and its architecture. 

![Figure 37](https://imgur.com/WZiDrPb.png)

## Results obtained in the SDGs transformation
The following sections describe the two options available under the SDG tab in the navigation bar.

### Overview
This page describes the transformation process of the Sustainable Development Goals into a SKOS taxonomy.  A word cloud graph displays the generated concepts for each SDG.  The image below shows the graph created from the first SDG.

![Figure 38](https://imgur.com/zlq7G3M.png)

Another example

![Figure 38.1](https://imgur.com/uFiOyRC.png)

### Concept graphs
 
The next image shows a graph created from the extracted concepts for each of the SDGs and their relationships.  

![Figure 39](https://imgur.com/P6VT16P.png)

There is an SDG browser that displays the concepts generated for each one in interactive graphs. (This is just a compact representation. The application shows these graphs one on top of the other with more space to interact with).

![Figure 39.1](https://imgur.com/igY4T3x.png)

### Related datasets
This page displays the relationships found between the SDGs and the extracted datasets from the CKAN platforms. The following graph displays the number of links generated for each SDG.

![Figure 40](https://imgur.com/kvunZsx.png)

Additionally, there is a table to browse every dataset that has a relationship with one or more SDG.

![Figure 41](https://imgur.com/Mf11o43.png)

## Tools
The tools page presents a dataset into DCAT triples transformer. This tool allows any user to obtain a complete semantic representation of the datasets extracted from the CKAN platforms.  The user can select the desired platforms and serialization format. Only signed-in administrators are allowed to see and use the administrative options.

![Figure 42](https://imgur.com/OCR3iqu.png)

## Extracted datasets
This page presents a browser to filter the extracted datasets from CKAN platforms. As of now,  there is an autocomplete title search bar and a filter for the extracted CKAN platforms.

![Figure 43](https://imgur.com/lqfcY3j.png)

Each dataset has a page to display its information. SDGs that have relationships with datasets will be listed on this page as well.

![Figure 44](https://imgur.com/3QICOSi.png)

## Administration
The administration menu presents the application configuration and the execution of the processes described in the architecture. The following sections detail each of its submenus.

### App configuration
The app uses a triplestore to store the generated knowledge base.  An administrator can easily change the triplestore when needed.

![Figure 45](https://imgur.com/s2I0es9.png)

### Datasets extractor
This application component manages the CKAN platforms used for datasets extraction. CKAN platforms can be added and deleted. The status displays whether or not the application has extracted data from a particular platform. The extraction history includes timestamps for every extraction and deletion and the amount of extracted datasets. The 'extract' button will store new datasets from the selected platforms. Finally, the 'Upload RDF' button will redirect to the tools page where that action is available.

![Figure 46](https://imgur.com/SMa4YMw.png)

### SDG extractor
This page runs the extraction and transformation process of the Sustainable Development Goals into a SKOS taxonomy.  Information about each step is detailed as well.

![Figure 47](https://imgur.com/xosGoii.png)

### SDG-OD Linker
This page allows the creation of links between the datasets and SDGs once they have been extracted and transformed.

![Figure 48](https://imgur.com/GnSgZ2l.png)

# Acknowledgement

The work has been funded and supported by the [Universidad Técnica Particular de Loja
(UTPL)](https://utpl.edu.ec).
