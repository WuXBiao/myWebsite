package main

import (
	"fmt"
	"sort"
)

/*
*
任务系统
输入一个列表，里面代表每个任务的开始结束时间。求最大的任务并发量是多少
[ [2,4], [1,5], [3,9]....  ]
*/
func main() {
	arr := [][]int{
		[]int{3, 4},
		[]int{1, 2},
		[]int{5, 9},
	}
	fmt.Println(getMaxRows(arr))
}

func getMaxRows(arr [][]int) int {
	if len(arr) == 0 {
		return 0
	}

	// 存储所有开始时间和结束时间
	starts := make([]int, len(arr))
	ends := make([]int, len(arr))

	for i := 0; i < len(arr); i++ {
		starts[i] = arr[i][0]
		ends[i] = arr[i][1]
	}

	sort.Ints(starts)
	sort.Ints(ends)

	var maxCnt, curCnt int = 0, 0
	i, j := 0, 0

	for i < len(arr) {
		// 双指针统计，如果是新的任务，并发+1，旧任务结束，并发-1
		if starts[i] < ends[j] {
			curCnt++
			if curCnt > maxCnt {
				maxCnt = curCnt
			}
			i++
		} else {
			// 并发-1
			curCnt--
			j++
		}
	}
	return maxCnt
}
