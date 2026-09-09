export type GraphNodeType = 'PERSON' | 'MOVIE';

export interface GraphNode {
  id: string;
  type: GraphNodeType;
  label: string;
  imagePath?: string;
  tmdbId?: number;
}

export interface GraphEdge {
  id: string;
  source: string;
  target: string;
  type: string;
  characterName?: string;
  job?: string;
}

export interface GraphResponse {
  root: GraphNode;
  nodes: GraphNode[];
  edges: GraphEdge[];
}
