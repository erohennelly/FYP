import { BasicGameComponentModel } from './basicGameComponentModel';
import { Player } from './player';

export class ServerUpdateModel {
  players: Array<Player>;
  flowers: Array<BasicGameComponentModel>;
  bees: Array<BasicGameComponentModel>;
}
