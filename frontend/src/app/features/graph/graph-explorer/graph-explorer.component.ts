import {
  AfterViewInit,
  Component,
  ElementRef,
  Input,
  OnDestroy,
  PLATFORM_ID,
  ViewChild,
  inject
} from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

import cytoscape, { Core, ElementDefinition } from 'cytoscape';
import { Router } from '@angular/router';

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

  private readonly router = inject(Router);

  private cy?: Core;

  private layoutFrame?: number;

  graphData: GraphResponse | null = null;

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
    if (this.layoutFrame !== undefined) {
      cancelAnimationFrame(this.layoutFrame);
    }

    this.cy?.stop();
    this.cy?.destroy();
    this.cy = undefined;
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
            'text-outline-color': '#0b1020',
            'background-image-crossorigin': 'null' as never
          }
        },

        {
          selector: 'node[type = "PERSON"]',
          style: {
            shape: 'ellipse',
            width: 200,
            height: 200,
            'background-color': '#4f46e5',
            'background-fit': 'cover',
            'background-clip': 'node',
            'border-width': 3,
            'border-color': '#818cf8'
          }
        },

        {
          selector: 'node[type = "MOVIE"]',
          style: {
            shape: 'round-rectangle',
            width: 225,
            height: 337.5,
            'background-color': '#f59e0b',
            'background-fit': 'cover',
            'background-clip': 'node',
            'border-width': 3,
            'border-color': '#fbbf24'
          }
        },

        {
          selector: 'node[imageUrl]',
          style: {
            'background-image': 'data(imageUrl)'
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
          selector: 'node.root[type = "MOVIE"]',
          style: {
            width: 180,
            height: 270
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
        }
      ],

      layout: {
        name: 'preset'
      }
    });

    this.registerEvents();
  }

  private renderGraph(): void {
    if (!this.cy || !this.graphData) {
      return;
    }

    if (this.layoutFrame !== undefined) {
      cancelAnimationFrame(this.layoutFrame);
    }

    this.cy.stop();
    this.cy.elements().remove();
    this.cy.add(this.createElements(this.graphData));

    this.runLayoutWhenReady();
  }

  private runLayoutWhenReady(): void {
    if (!this.cy) {
      return;
    }

    const container = this.cy.container();

    if (!container || container.clientWidth === 0 || container.clientHeight === 0) {
      this.layoutFrame = requestAnimationFrame(() => this.runLayoutWhenReady());
      return;
    }

    this.cy.resize();

    this.cy
      .layout({
        name: 'concentric',
        animate: false,
        fit: true,
        padding: 60,
        minNodeSpacing: 56,
        concentric: node => (node.hasClass('root') ? 2 : 1),
        levelWidth: () => 1
      })
      .run();
  }

  private createElements(
    graph: GraphResponse
  ): ElementDefinition[] {
    const nodes = graph.nodes.map((node: GraphNode) => {
      const imageUrl = this.getNodeImageUrl(
        node.imagePath,
        node.type
      );

      return {
        group: 'nodes' as const,

        data: {
          id: node.id,
          label: node.label,
          type: node.type,
          imagePath: node.imagePath,
          tmdbId: node.tmdbId,
          ...(imageUrl ? { imageUrl } : {})
        },

        classes: node.id === graph.root.id ? 'root' : ''
      };
    });

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
      const type = node.data('type') as string;
      const nodeId = String(node.id());
      const tmdbId = node.data('tmdbId') as number | undefined;

      if (type === 'PERSON') {
        const personId = nodeId.replace(/^person-/, '');

        if (this.router.url.startsWith(`/person/${personId}`)) {
          return;
        }

        void this.router.navigate(['/person', personId], {
          queryParams: tmdbId ? { tmdbId } : {}
        });
        return;
      }

      if (type === 'MOVIE') {
        const movieId = nodeId.replace(/^movie-/, '');

        if (this.router.url.startsWith(`/movie/${movieId}`)) {
          return;
        }

        void this.router.navigate(['/movie', movieId]);
      }
    });
  }

  private getNodeImageUrl(
    imagePath?: string,
    type?: GraphNode['type']
  ): string | undefined {
    if (!imagePath) {
      return undefined;
    }

    const size = type === 'MOVIE' ? 'w342' : 'w185';

    return `https://image.tmdb.org/t/p/${size}${imagePath}`;
  }
}
