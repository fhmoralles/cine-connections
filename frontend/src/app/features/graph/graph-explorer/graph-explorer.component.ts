import {
  AfterViewInit,
  Component,
  ElementRef,
  Input,
  OnDestroy,
  PLATFORM_ID,
  ViewChild,
  inject,
  signal
} from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

import cytoscape, { Core, ElementDefinition } from 'cytoscape';

import { environment } from '../../../../environments/environment';
import {
  GraphEdge,
  GraphNode,
  GraphResponse
} from '../../../core/models/graph.model';

@Component({
  selector: 'app-graph-explorer',
  standalone: true,
  templateUrl: './graph-explorer.component.html',
  styleUrl: './graph-explorer.component.scss'
})
export class GraphExplorerComponent implements AfterViewInit, OnDestroy {
  @ViewChild('graphContainer')
  graphContainer!: ElementRef<HTMLDivElement>;

  @Input()
  set graph(value: GraphResponse | null) {
    this.graphData = value;

    if (this.cy && value) {
      this.renderGraph();
    }
  }

  private readonly platformId = inject(PLATFORM_ID);

  private cy?: Core;

  graphData: GraphResponse | null = null;

  selectedNode = signal<GraphNode | null>(null);

  ngAfterViewInit(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    this.initializeGraph();

    if (this.graphData) {
      this.renderGraph();
    }
  }

  ngOnDestroy(): void {
    this.cy?.destroy();
  }

  private initializeGraph(): void {
    this.cy = cytoscape({
      container: this.graphContainer.nativeElement,

      elements: [],

      style: [
        {
          selector: 'node',
          style: {
            label: 'data(label)',
            'text-valign': 'bottom',
            'text-halign': 'center',
            'font-size': '11px',
            'text-margin-y': 8,
            color: '#ffffff',
            'text-outline-width': 2,
            'text-outline-color': '#0b1020'
          }
        },

        {
          selector: 'node[type = "PERSON"]',
          style: {
            shape: 'ellipse',
            width: 70,
            height: 70,
            'background-color': '#4f46e5',
            'border-width': 3,
            'border-color': '#818cf8'
          }
        },

        {
          selector: 'node[type = "MOVIE"]',
          style: {
            shape: 'round-rectangle',
            width: 90,
            height: 120,
            'background-color': '#f59e0b',
            'border-width': 3,
            'border-color': '#fbbf24'
          }
        },

        {
          selector: 'node.root',
          style: {
            'border-width': 6,
            'border-color': '#ffffff'
          }
        },

        {
          selector: 'edge',
          style: {
            width: 2,
            'line-color': '#64748b',
            'curve-style': 'bezier',
            'target-arrow-shape': 'none'
          }
        },

        {
          selector: ':selected',
          style: {
            'border-color': '#22c55e',
            'border-width': 6
          }
        }
      ],

      layout: {
        name: 'cose',
        animate: true,
        padding: 50
      }
    });

    this.registerEvents();
  }

  private renderGraph(): void {
    if (!this.cy || !this.graphData) {
      return;
    }

    this.cy.elements().remove();

    const elements = this.createElements(this.graphData);

    this.cy.add(elements);

    this.cy
      .layout({
        name: 'cose',
        animate: true,
        padding: 70,
        nodeRepulsion: () => 8000,
        idealEdgeLength: () => 180
      })
      .run();
  }

  private createElements(
    graph: GraphResponse
  ): ElementDefinition[] {
    const nodes = graph.nodes.map((node: GraphNode) => ({
      group: 'nodes' as const,

      data: {
        id: node.id,
        label: node.label,
        type: node.type,
        imagePath: node.imagePath,
        tmdbId: node.tmdbId
      },

      classes: node.id === graph.root.id ? 'root' : ''
    }));

    const edges = graph.edges.map((edge: GraphEdge) => ({
      group: 'edges' as const,

      data: {
        id: edge.id,
        source: edge.source,
        target: edge.target,
        type: edge.type,
        characterName: edge.characterName,
        job: edge.job
      }
    }));

    return [...nodes, ...edges];
  }

  private registerEvents(): void {
    if (!this.cy) {
      return;
    }

    this.cy.on('tap', 'node', event => {
      const node = event.target;

      const nodeData = this.graphData?.nodes.find(
        (item: GraphNode) => item.id === node.id()
      );

      if (nodeData) {
        this.selectedNode.set(nodeData);
      }
    });

    this.cy.on('tap', event => {
      if (event.target === this.cy) {
        this.selectedNode.set(null);
      }
    });
  }

  getImageUrl(imagePath?: string): string | null {
    if (!imagePath) {
      return null;
    }

    return `${environment.tmdbImageUrl}${imagePath}`;
  }
}
