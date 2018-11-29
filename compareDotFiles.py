import pydot as pd
import sys

if (len(sys.argv) != 3): 
    print("python compareDotFiles.py <path_to_first_dot_file> <path_to_second_dot_file>")

# Paths
first_graph_path = "/Users/jason/MalwareProject/EwindOutput/ewind_linux_old.dot"
second_graph_path = "/Users/jason/MalwareProject/EwindOutput/ewind_linux.dot"

# Load the graphs from the dot files
first_graph = pd.graph_from_dot_file(first_graph_path)[0]
second_graph = pd.graph_from_dot_file(second_graph_path)[0]

# Get a set of the edges for both graphs
first_graph_edges = set(first_graph.get_edges())
second_graph_edges = set(second_graph.get_edges())


print("The number of edges in first graph is ", len(first_graph_edges))
print("The number of edges in second graph is ", len(second_graph_edges))

# Find edges in common
edges_intersection = first_graph_edges.intersection(second_graph_edges)
print("The number of edges in common is ", len(edges_intersection))

# Find edges in first_graph that's not in second graph
edges_first_unique = first_graph_edges.difference(second_graph_edges)
print("The number of edges unique in first graph is ", len(edges_first_unique))

print("\nUnique Edges for first graph : ")
for edge in edges_first_unique:
    print(edge.to_string())
print("\n")


# Find edges in second_graph that's not in first graph
edges_second_unique = second_graph_edges.difference(first_graph_edges)
print("The number of edges unique in second graph is ", len(edges_second_unique))


print("\nUnique Edges for second graph : ")
for edge in edges_second_unique:
    print(edge.to_string())
print("\n")




