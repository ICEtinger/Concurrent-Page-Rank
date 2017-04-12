# Concurrent-Page-Rank

This project contains classes to get statistical values from graphs. 
The function main:
• builds a graph from a CSV file (by calling the class GraphBuilder) that represent emails being sent between email addresses,
• calculates up to 6 types of centrality (with class PageRank), 
• calculates statistical values from the graph (with class getStatsFromCollection),
• and prints the results in a CSV file (with class GraphWriter).

The function main contains the parameters to specify which centrality types and which statistical values should be calculated, 
as well as parameters about how the PageRank algorithm should operate (namely the number of Threads, the damping factor of 4 types of 
centrality, the damping and bias factor of 2 types of centrality, and when to stop iterating the loop that calculates the centrality). 
It also contains a parameter to sort the nodes by a specified type of centrality before printing.
It can calculate the following values about each node in the graph (email addresses):
                                             the 6 types of centrality specified at centralityType.java
	                                         (namely Standard, Reversed, Neighbor, and their versions with unweighted edges),
	                                         and the in-degree and out-degree of each Node.
The statistical values it calculates about the graph is, for each email sent:
                                             the String of the email-identifier,
	                                         the values(^) of the Node sending the email, and statistical results of the Collection of the 
	                                         values(^) of the Nodes receiving the email, those statistical results are: 
	                                       { the mean, the median, the standard deviation, the harmonic mean, and the mean absolute deviation }
