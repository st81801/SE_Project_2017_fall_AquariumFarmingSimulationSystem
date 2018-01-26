// 修改85行: 飽食度之下界為0
// 新增96~99行: 餓死判斷
// 2018/1/9 edit by yusiang

    影響因子:
/*
/*  fishAttribute: lifeTime, familiarity, weight, lively, sick, satiation, hurt, death
/*. otherAttr      lifeEnd, maxSatiation, maxWeight, status, waterPlant.death, maxNumOfFish
/*  event: fight, snatch, sick, death, satiation
/*  environmentAttribute: waterQuality, waterTemperature, stool, oxygen, fishTankSize, waterPlants
/*  utility: feeder, filter, airPump, heater, lighter, decoration

/*  fishType: 10種
/* 	0 ~ 100 is a range, 100 means that must be happened, 0 means not 刻度的概念，放在屬性上為計數，事件則為發生機率
/* 	# means the parameter of each influence
/* 	rand(p) is a probability random from 0 ~ 100
/*     	if p > 該屬性的值
/*	        event happened
/* 	check means timer should judge after 1 period of time (1 hour)20
/*  一尺等於30.3公分
/*  2尺缸(60*30*30) / 3尺缸(90*45*60) / 4尺缸(120*45*60)
/*  totalMaxWeight: 1800,  4050, 5400
/*  totalMaxAmount: 10, 15, 20
/*  (比例)為範圍0~100%
/*  (實際)為魚種清單數值或是由數值經數學運算代換而得
/*  魚的部分參照魚種清單，設備參照設備清單
/*---------------------------------------------------------------------------------------------------------------------->
*/	魚內部():		// init status = HEALTHY
		// enum status (HEALTHY, SICKNESS, DEATH) 魚的狀態
		// if (death == 100) myFish.status = DEATH
	o	(實際)生命週期: lifeTime	// init: 0 /*範圍參照魚種清單*/
	o		check:	if (systemTimer is running && myFish.lifeTime < myFish.lifeEnd)
						myFish.lifeTime++		// define fish 的 lifeTime (每隻魚各有不同)
	o		# 死亡
				if (myFish.lifeTime == myFish.lifeEnd)	// unsigned int lifeEnd = fishType.lifeEnd
					myFish.status = DEATH	
	o		# 氧氣量
				if (myFish.lifeTime < myFish.lifeEnd && systemTimer pass 1 period of time)
					fishTank.oxygen-0.1			
			/* destructor of the fish is executed if throw the fish */
	o	(比例)對環境的熟悉度: familiarity	// init: 0
	o		check: 	if (systemTimer is running && myFish.familiarity < 100)
						myFish.familiarity++
	o		# 活潑度
				if (myFish.familiarity++)
					myFish.lively++
	o		# 搶食
				丟食物時
				if (myFish.familiarity++)
					myFish.snatch++
	o		# 打架
				if (myFish.familiarity++ && systemTimer pass 3 period of time)
					myFish.fight++
	o	(實際)體重: weight 	// init: fishType.weight +- rand(0 ~ 2) /* weight = myFish.length * 10 */
			check: 	if (myFish.weight == myFish.maxWeight)
						/* myFish.weight 將保持在最大值不再增加 */ 
	o		# 打架
				if (myFish.weight++ && systemTimer pass 3 period of time)
					myFish.fight++
	o		# 搶食
				if (myFish.weight++ && systemTimer pass 2 period of time)
					myFish.snatch++
	o	(比例)活潑度: lively	// init: fishType +- rand(0 ~ 3)
			# 打架
	o			if (myFish.lively++)
					myFish.fight++
			# 搶食
	o			if (myFish.lively++)
					myFish.snatch++
	o	(比例)生病: sick   // init: 0
	o		check: 	if (myFish.sick >= 60)
						myFish.status = SICKNESS 	
	o		# 死亡
				if (myFish.sick >= 60)
					myFish.death++
	o		# 打架
				if (myFish.sick >= 45)
					myFish.fight--
	o		# 飽食度
				if (myFish.sick >= 30)
					myFish.maxSatiation--	// maxSatiation is defined by fishType
	o		# 活潑度
				if (myFish.sick >= 25)
					myFish.lively--
	o	(實際)飽食度: satiation 	// init: 100    lower bound: 0; full /* 等於體重 */
	o		check:	if (systemTimer is running)
						myFish.satiation--
	o				if (satiation == 100)
						display 吃飽了
	o		# 死亡
				// 吃太多的狀況
				if (myFish.satiation > 100)
					myFish.death++
				else if (myFish.satiation > 200)
					myFish.death = 100
				// 快餓死的狀況
				if (20 < myFish.satiation <= 40 && systemTimer pass 1 day)
						myFish.death++
				else if (myFish.satiation <= 20 && systemTimer pass 1 day)
	o		# 活潑度
				if (myFish.satiation++ && myFish.satiation <= 100)
					myFish.lively++
				else if (myFish.satiation > 100 && systemTimer pass 1 period of time)
					myFish.lively--
	o		# 體重
				if (myFish.satiation >= 90 && systemTimer pass 4 period of time && myFish.weight < myFish.maxWeight)
					myFish.weight++
	o	(實際)受傷: hurt 		// init: 0 /* 等於體重 */
	o		# 死亡
				if (myFish.hurt >= 60)
					myFish.death++;
	o		# 生病
				if (myFish.hurt >= 50)
					myFish.sick++;
	o	(比例)死亡指數: death 	// init: 0
	o		check: 	if (myFish.death == 100)
						myFish.status = DEATH
					else if (80 <= myFish.death < 100)
						/* display 頻死狀態 */
	o		# 水質
				if (fishTank.numOfDeadFish() > 0 && deadFishAllClear() == false)
					fishTank.waterQuality -= fishTank.numOfDeadFish() *0.1
			# 氧氣量
				if (fishTank.numOfDeadFish() > 0 && systemTimer pass 1 period of time)
					fishTank.oxygen -= fishTank.numOfDeadFish()*0.1
		
//---------------------------------------------------------------------------------------------------------------------->
	魚與魚之間:
	o	(比例)打架: fight 	//init: fishType +- rand(0 ~ 2)				
			if (myFish1 meet myFish2)	// check position
			{
				N = rand(0 ~ 100)
				if(N < myFish1.fight && N < myFish2.fight)
					fight()
			}
	o		check: 	if (systemTimer pass 4 period of time)
						myFish.fight--
	o		# 受傷
				if (fight())
					b.hurt += a.fight - b.fight
					a.hurt += (a.fight - b.fight) / 2
		(比例)搶食: snatch 	//init: fishType +- rand(0 ~ 2) /* 起始搶食等於起始活潑度 */
			//if (myFish1.satiation < 50 && myFish2.satiation < 50)
			{
				snatch()		改成餵食時分配食物
			}
			//# 飽食度
				//if (myFish1.snatch > myFish2.snatch)
				/* If the food is not enough for all the fish, a might eat food first. */
//---------------------------------------------------------------------------------------------------------------------->
	環境因素:
	o	(比例)水質: waterQuality	//init: 100 ; means the appropriate state of this fishTank
	o		check: 	if (systemTimer pass 6 period of time)
						fishTank.waterQuality--
					if (waterQuality < 50)
						/* display 水質不良 */
					else (waterQuality < 25)
						/* display 水質糟糕 */
	o		# 死亡
				if (25 < fishTank.waterQuality <= 50)
					myFish.death++
				else if (15 < fishTank.waterQuality <= 25)
					myFish.death += 2
				else if (fishwaterQuality <= 15)
					myFish.death += 3
	o		# 水草
				if (50 < fishTank.waterQuality <= 70)
					waterPlant.death++
				else if (30 < fishTank.waterQuality <= 50)
					waterPlant.death += 2
				else if (fishTank.waterQuality < 30)
					waterPlant.death += 3
		(實際)水溫: waterTemperature 		//init: 50 ; means normal temperature for this fishTank /*range = 18.0 ~ 38.0*/
			
	o		每天下降1度直到最低溫為止
			# 水草
				if (31 < fishTank.waterTemperature <= 33 || 23 < fishTank.waterTemperature <= 25
					waterPlant.death++
				else if (fishTank.waterTemperature > 33 || fishTank.waterTemperature <= 23)
					waterPlant.death += 2
			# 死亡
				//海水魚 24~28
				if (28 < fishTank.waterTemperature <= 32 || 21 < fishTank.waterTemperature <= 24
					myFish.death++
				else if (fishTank.waterTemperature > 32 || fishTank.waterTemperature <= 21)
					myFish.death += 2
				//淡水魚 18~28
				if (28 < fishTank.waterTemperature <= 33 || 18 <= fishTank.waterTemperature <= 22
					myFish.death++
				else if (fishTank.waterTemperature > 33)
					myFish.death += 2
	o	(實際)餵食器: feeder //  init: 100 ; feeder has enough food
		// assume each of food provide the same energy, each one may let satiation add 3(or ruled by us)
			check: 	if (feeder.amount < 100)
					/* user can call feeder.addFood() or feeder.addFoodToFull() */
	o	(實際)過濾器: filter // init: off ; opened by user call openFilter()
			check:	if (filter.state == open && systemTimer pass 2 period of time)
	o					fishTank.waterQuality++
	o	(實際)水草: waterPlants // init: 0 (0株水草) ; add by user call addWaterPlant ; maxNumber = 2, 4, 6(依據魚缸大小可自訂)
			check: if(systemTimer pass 1 period of time)
						display fishTank.numberOfWaterplants()
	o		# 氧氣量
				if (fishTank.oxygen < 100 && fishTank.numberOfWaterplants() > 0)
	o				fishTank.oxygen += numberOfWaterplants()
		
	o	(比例)糞便: stool // init: 0 魚的糞便視為一樣大，排便一次一粒
	o		check: 	if (存活每過1天)
						myFish.defecation() // 排便
						fishTank.stool++
	o		# 水質
				if (0 < fishTank.stool <= 20 && systemTimer pass 3 period of time)
					fishTank.waterQuality--
				if (fishTank.stool > 30)
					fishTank.waterQuality -= fishTank.stool / fishTank.numOfFish()
	o	(實際)魚缸大小: fishTankSize // init size
			// enum fishTank.size(SMALL. MEDIUM, LARGE) /* 2, 3, 4尺 */
		
					if (fishTank.numOfFish == fishTank.maxNumOfFish)
						/* display 魚缸已滿 */
	o		# 水質:
				if (fishTank.size == SMALL)
					fishTank.waterQuality -= 3
				else if (fishTank.size == MEDIUM)
					fishTank.waterQuality -= 2
				else if (fishTank.size == LARGE)
					fishTank.waterQuality -= 1
	o	(實際)打氣泵: airPump // init: off ; openAirPump() to open
			check: 	if (airPump.state == open && systemTimer pass 2 period of time)
	o					fishTank.oxygen++
		(實際)照明: light	// init: off ; not consider sunlight ; openLight() to open
			check: 	if (light.state == open && systemTimer pass 3 period of time)
						for(all waterPlants in fishTank)
	o						waterPlant[i].death--
	o	(實際)擺設: (岩石, 假珊瑚) decoration // init: position ; move: user call moveDecoration() ; remove : removeDecoration()
			# 打架
				if (fight()) // myFish1.fight > myFish2.fight
					if (myFish2.position is closed to the fishTank.decoration[i]) 
						myFish2.hideInDecoration(i)
						fight() cancelled
	o	(比例)氧氣量: oxygen // init: 100 ; enough for this fishTank
	o		check:	if (systemTimer is running)
						fishTank.oxygen-0.1
	o		# 死亡
				if (25 < fishTank.oxygen < 50)
					myFish1.death++
				else if (10 < fishTank.oxygen <= 25)
					myFish1.death += 2
				else if (fishTank.oxygen <= 10)
					myFish1.death += 3

			/********************************
			 *  溫度(度c) | 飽和含氧量(mg/L)*
			 *------------|-----------------*
			 *		18	  |			9.47	*
			 *		19	  |			9.27	*
			 *		20    |			9.09	*
			 *		21    |			8.91	*
			 *		22    |			8.74	*
			 * 		23    |			8.57	*
			 *		24    |			8.41	*
			 *		25    |			8.25	*
			 *		26    |			8.11	*
			 *		27    |			7.96	*
			 *		28    |			7.83    *
			 ********************************/
	o	(實際)加溫器: heater // init: off ; user call openHeater() to open
			check: 	if (heater.state == open)
						if (systemTimer pass 2 period of time)
	o						fishTank.waterTemperature++
/*---------------------------------------------------------------------------------------------------------------------->
			// extra part
		水流動: waterFlow
			#搶食
			#水質
		細菌: bacterial
			#水質
			#pH值
			#生病
		追逐: chase
			#打架
		換水: (水質穩定劑) waterChange
			#細菌
			#pH值
		餌料殘渣: foodResidue
			#水質
			#過濾器
		勢力範圍: sphereOfInfluence
			#打架
			#追逐
		pH值: pH
			#死亡
			#水草
			//#細菌

*/