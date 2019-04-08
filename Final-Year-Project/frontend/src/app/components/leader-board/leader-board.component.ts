import { Component, OnInit } from '@angular/core';
import { DataService } from '../../services/data.service';

export interface Transaction {
  userName: string;
  points: number;
}

@Component({
  selector: 'app-posts',
  templateUrl: './leader-board.component.html',
  styleUrls: ['./leader-board.component.scss']
})
export class LeaderBoardComponent implements OnInit {
  displayedColumns: string[] = ['userName', 'points'];
  transactions: Transaction[] = [];

  constructor(private data: DataService) { }

  ngOnInit() {
    this.data.getPlayers().subscribe(
      data => this.transactions = data.playerLists
    );
  }
}
