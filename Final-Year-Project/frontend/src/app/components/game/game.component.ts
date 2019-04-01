import {
  Component,
  OnInit,
  HostListener,
  ElementRef,
  ViewChild,
} from '@angular/core';

import { Player } from './../../models/player';
import { Movement } from '../../models/movement';
import { AppComponent } from '../../app.component';
import { GameService } from '../../services/game.service';
import { ServerUpdateModel } from 'src/app/models/serverUpdate';
import { ServerMessage } from 'src/app/models/serverMessage';
import { AttackModel } from 'src/app/models/attckModel';
import { BasicGameComponentModel } from 'src/app/models/basicGameComponentModel';

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})


export class GameComponent implements OnInit {
  constructor(private game: GameService, private appComponent: AppComponent) {}

  @ViewChild('myCanvas') myCanvas: ElementRef;
  public context: CanvasRenderingContext2D;
  private width = 1500;
  private height = 700;
  private player: Player;
  private playerArray: Array<Player> = [];
  private flowerArray: Array<BasicGameComponentModel> = [];
  private beesArray: Array<BasicGameComponentModel> = [];
  private score = 0;
  private userName: string = this.appComponent.username || 'user' + Math.floor((Math.random() * 1000) + 1);

  private flowersPositions = [
    [0, 40],
    [40, 0],
    [-40, 0],
    [0, -40],
    [-27, -27],
    [27, 27],
    [-27, 27],
    [27, -27],
  ];

  ngOnInit() {
    this.context = (this.myCanvas.nativeElement as HTMLCanvasElement).getContext('2d');

    this.game.gameUpdates.subscribe(msg => {
      // console.log({ msg });
      const data: ServerUpdateModel = msg;
      this.playerArray = data.players;
      this.flowerArray = data.flowers;
      this.beesArray = data.bees;
      this.updateScore();
      this.draw();
    });

    this.game.playerJoins.subscribe(msg => {
      this.player = msg;
    });

    this.game.sendPlayerJoin(this.userName);
  }
  //
  //
  // ngAfterViewInit(): void {
  //   this.draw();
  // }
  //
  // ngOnDestroy(): void {
  //   this.game.sendPlayerLeave(this.userName);
  // }

  updateScore() {
    if (this.player) {
      this.playerArray.map(player => {
        if (player.id === this.player.id) {
          this.player = player;
          this.score = player.points;
        }
      });
    }
  }


  draw() {
    const ctx = this.context;
    const HIT_COLOR = '#06080c';
    ctx.fillStyle = HIT_COLOR;
    ctx.fillRect(0, 0, this.width, this.height);

    this.flowerArray.map(flower => {
      this.flowersPositions.forEach(flowerPos => {
        ctx.fillStyle = '#ffffff';
        ctx.beginPath();
        ctx.arc(flower.xpos + flowerPos[0], flower.ypos + flowerPos[1], 18, 0, 2 * Math.PI, true);
        ctx.fill();
      });

      ctx.fillStyle = '#FFFF00';
      ctx.beginPath();
      ctx.arc(flower.xpos, flower.ypos, 30, 0, 2 * Math.PI, true);
      ctx.fill();
    });

    this.playerArray.map(player => {
      const colors =  player.color.values();
      ctx.fillStyle = colors.next().value;

      const oneThirdLength = player.length / 3;

      ctx.fillRect(player.xpos, player.ypos, player.length, oneThirdLength);
      ctx.fillRect(player.xpos, player.ypos + oneThirdLength * 2, player.length, oneThirdLength);
      ctx.fillStyle = colors.next().value;
      ctx.fillRect(player.xpos, player.ypos + oneThirdLength, player.length, oneThirdLength);
    });

    this.beesArray.map(bee => {
      const radius = bee.length;
      const colors =  bee.color.values();

      ctx.fillStyle = colors.next().value;
      ctx.beginPath();
      ctx.arc(bee.xpos, bee.ypos, radius, 0, Math.PI, true);
      ctx.fill();

      ctx.fillStyle = colors.next().value;
      ctx.beginPath();
      ctx.arc(bee.xpos, bee.ypos, radius, Math.PI, 2 * Math.PI, true);
      ctx.fill();
    });
  }

  @HostListener('click', ['$event'])
  onClick(btn: MouseEvent) {
    const xLocation = btn.clientX - this.context.canvas.getBoundingClientRect().left;
    const yLocation = btn.clientY - this.context.canvas.getBoundingClientRect().top;
    this.playerArray
      .filter(player => player.id !== this.player.id)
      .map(player => {
        if (Math.abs(player.xpos - xLocation) < player.length &&
          Math.abs(player.ypos - yLocation) < player.length) {
            const attackModel = new AttackModel();
            attackModel.sender = this.player;
            attackModel.target = player;
            const serverMessage = new ServerMessage();
            serverMessage.attack = attackModel;
            this.game.sendGameUpdates(serverMessage);
          }
      });
 }

//  @HostListener('mousemove', ['$event'])
//   oninput(input: MouseEvent) {
//     const movement = new Movement();
//     const serverMessage = new ServerMessage()
//     serverMessage.movement = movement
//     movement.userName = this.userName;
//     movement.xMovement = input.movementX !== 0 ? (input.movementX > 0 ? 1 : 0) : 0;
//     movement.yMovement = input.movementY !== 0 ? (input.movementY > 0 ? 1 : -1) : 0;
//     this.game.sendGameUpdates(serverMessage)

//     // console.log('input.movementX', movement.xMovement)
//     // console.log('input.movementY', movement.yMovement)
//   }

  @HostListener('window:keydown', ['$event'])
  keyEvent(event: KeyboardEvent) {
    const movement = new Movement();
    const serverMessage = new ServerMessage();
    serverMessage.movement = movement;
    const id: string = this.player.id;

    switch (event.key) {
      case 'ArrowDown':
      movement.id = id;
      movement.xMovement = 0;
      movement.yMovement = 10;

      this.game.sendGameUpdates(serverMessage);
      break;

      case 'ArrowUp':
      movement.id = id;
      movement.xMovement = 0;
      movement.yMovement = -10;
      this.game.sendGameUpdates(serverMessage);
      break;

      case 'ArrowLeft':
      movement.id = id;
      movement.xMovement = -10;
      movement.yMovement = 0;
      this.game.sendGameUpdates(serverMessage);
      break;

      case 'ArrowRight':
      movement.id = id;
      movement.xMovement = 10;
      movement.yMovement = 0;
      this.game.sendGameUpdates(serverMessage);
      break;
    }
  }
}
